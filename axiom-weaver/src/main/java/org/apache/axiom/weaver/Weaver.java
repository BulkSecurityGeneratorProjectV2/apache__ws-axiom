/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.weaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.weaver.mixin.ClassDefinition;
import org.apache.axiom.weaver.mixin.Mixin;
import org.apache.axiom.weaver.mixin.clazz.MixinFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.veithen.jrel.association.MutableReferences;
import com.github.veithen.jrel.collection.LinkedIdentityHashSet;

public final class Weaver {
    private static final Log log = LogFactory.getLog(Weaver.class);

    private final ImplementationClassNameMapper implementationClassNameMapper;
    private final Map<Class<?>, ImplementationNode> nodeByInterface = new HashMap<>();
    private final Map<Class<?>, Set<Mixin>> mixinsByInterface = new HashMap<>();
    private final MutableReferences<ImplementationNode> nodes = Relations.WEAVER.getConverse().newReferenceHolder(this);
    private int nextId = 1;

    public Weaver(ImplementationClassNameMapper implementationClassNameMapper) {
        this.implementationClassNameMapper = implementationClassNameMapper;
    }

    public void loadWeavablePackage(ClassLoader classLoader, String packageName) {
        for (Mixin mixin : MixinFactory.loadMixins(new ClassFetcherImpl(classLoader), packageName)) {
            addMixin(mixin);
        }
    }

    private void addMixin(Mixin mixin) {
        Class<?> iface = mixin.getTargetInterface();
        Set<Mixin> mixins = mixinsByInterface.get(iface);
        if (mixins == null) {
            mixins = new HashSet<>();
            mixinsByInterface.put(iface, mixins);
        }
        mixins.add(mixin);
    }

    private ImplementationNode addInterface(Class<?> iface) {
        ImplementationNode node = nodeByInterface.get(iface);
        if (node == null) {
            Set<ImplementationNode> parents = new HashSet<>();
            for (Class<?> superClass : iface.getInterfaces()) {
                parents.add(addInterface(superClass));
            }
            Set<Mixin> mixins = mixinsByInterface.get(iface);
            if (mixins == null) {
                mixins = new HashSet<>();
            } else {
                for (Mixin mixin : mixins) {
                    for (Class<?> addedIface : mixin.getAddedInterfaces()) {
                        if (addedIface != iface) {
                            parents.add(addInterface(addedIface));
                        }
                    }
                }
            }
            node = new ImplementationNode(nextId++, parents, iface, mixins);
            nodeByInterface.put(iface, node);
            nodes.add(node);
        }
        return node;
    }

    public void addInterfaceToImplement(Class<?> iface) {
        addInterface(iface).requireImplementation();
    }

    private void compact() {
        while (true) {
            boolean updated = false;
            for (ImplementationNode node : nodes) {
                updated |= node.compact();
            }
            if (!updated) {
                break;
            }
        }
    }

    private void promoteCommonMixins() {
        while (true) {
            boolean updated = false;
            for (ImplementationNode node : nodes) {
                updated |= node.promoteCommonMixins();
            }
            if (!updated) {
                break;
            }
        }
    }

    private void dump(String prefix, boolean showImplName) {
        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder(prefix);
            builder.append("digraph G {\n  rankdir = BT;\n  node [shape=box];\n");
            for (ImplementationNode node : nodes) {
                node.dump(builder, showImplName ? implementationClassNameMapper : null);
            }
            builder.append("}");
            log.debug(builder.toString());
        }
    }

    public ClassDefinition[] generate() {
        dump("Initial graph:\n", false);

        nodes.forEach(ImplementationNode::removeIfNotRequired);
        dump("Graph after removing nodes that are not required:\n", false);

        Map<Set<ImplementationNode>,Set<ImplementationNode>> mergableNodes = new HashMap<>();
        for (ImplementationNode node : nodes) {
            mergableNodes.computeIfAbsent(node.getRequiredDescendantsOrSelf(), k -> new LinkedIdentityHashSet<>()).add(node);
        }
        for (Set<ImplementationNode> nodes : mergableNodes.values()) {
            if (nodes.size() > 1) {
                ImplementationNode.merge(nodes);
            }
        }
        dump("Graph after merging nodes:\n", false);
        
        nodes.forEach(ImplementationNode::reduce);
        dump("Graph after applying transitive reduction:\n", false);
        
//        compact();
//        dump("Graph after compaction:\n", false);

        nodes.forEach(ImplementationNode::ensureSingleParent);
        dump("Graph after removing multiple inheritance:\n", false);

        // TODO: do this in a loop until there are no more updates
        compact();
        promoteCommonMixins();
        compact();
        dump("Final graph:\n", true);
        List<ClassDefinition> result = new ArrayList<>();
        for (ImplementationNode node : nodes) {
            result.addAll(node.toClassDefinitions(implementationClassNameMapper));
        }
        return result.toArray(new ClassDefinition[result.size()]);
    }

    public ClassLoader toClassLoader(ClassLoader parent) {
        return new WeavingClassLoader(parent, generate());
    }
}
