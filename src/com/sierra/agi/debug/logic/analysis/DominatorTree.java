package com.sierra.agi.debug.logic.analysis;

import java.util.*;

/**
 * An implementation of the O(n log n) Lengauer-Tarjan algorithm for building
 * the Dominator Tree for a flow graph.
 */
public class DominatorTree {

    /**
     * Semidominator numbers by block.
     */
    private final Map<BasicBlock, Integer> semi;

    /**
     * Parents by block.
     */
    private final Map<BasicBlock, BasicBlock> parent;

    /**
     * Predecessors by block.
     */
    private final Multimap<BasicBlock> pred;

    /**
     * Blocks in DFS order; used to look up a block from its semidominator
     * numbering.
     */
    private final ArrayList<BasicBlock> vertex;

    /**
     * Blocks by semidominator block.
     */
    private final Multimap<BasicBlock> bucket;

    /**
     * idominator map, built iteratively.
     */
    private final Map<BasicBlock, BasicBlock> idom;

    /**
     * Dominance frontiers of this dominator tree, built on demand.
     */
    private Multimap<BasicBlock> dominanceFrontiers;

    /**
     * Dominator tree, built on demand from the idominator map.
     */
    private Multimap<BasicBlock> dominatorTree;

    /**
     * Auxiliary data structure used by the O(m log n) eval/link implementation:
     * ancestor relationships in the forest (the processed tree as it's built
     * back up).
     */
    private final Map<BasicBlock, BasicBlock> ancestor;

    /**
     * Auxiliary data structure used by the O(m log n) eval/link implementation:
     * node with least semidominator seen during traversal of a path from node
     * to subtree root in the forest.
     */
    private final Map<BasicBlock, BasicBlock> label;

    /**
     * A topological traversal of the dominator tree, built on demand.
     */
    private LinkedList<BasicBlock> topologicalTraversalImpl;

    /**
     * Constructor for DominatorTree.
     *
     * @param controlFlowGraph The ControlFlowGraph to build the DominatorTree for.
     */
    public DominatorTree(ControlFlowGraph controlFlowGraph) {
        this.semi = new HashMap<BasicBlock, Integer>();
        this.parent = new HashMap<BasicBlock, BasicBlock>();
        this.pred = new Multimap<BasicBlock>();
        this.vertex = new ArrayList<BasicBlock>();
        this.bucket = new Multimap<BasicBlock>();
        this.idom = new HashMap<BasicBlock, BasicBlock>();
        this.ancestor = new HashMap<BasicBlock, BasicBlock>();
        this.label = new HashMap<BasicBlock, BasicBlock>();

        this.depthFirstSearch(controlFlowGraph);
        this.computeDominators();
    }

    /**
     * Create and/or fetch the map of immediate dominators.
     *
     * @return the map from each block to its immediate dominator (if it has one).
     */
    public Map<BasicBlock, BasicBlock> getIdoms() {
        return this.idom;
    }

    /**
     * Compute and/or fetch the dominator tree as a Multimap.
     *
     * @return the dominator tree.
     */
    public Multimap<BasicBlock> getDominatorTree() {
        if (this.dominatorTree == null) {
            this.dominatorTree = new Multimap<BasicBlock>();

            for (BasicBlock node : this.idom.keySet()) {
                dominatorTree.get(this.idom.get(node)).add(node);
            }
        }

        return this.dominatorTree;
    }

    /**
     * Compute and/or fetch the dominance frontiers as a Multimap.
     *
     * @return a Multimap where the set of nodes mapped to each key node is the
     * set of nodes in the key node's dominance frontier.
     */
    public Multimap<BasicBlock> getDominanceFrontiers() {
        if (this.dominanceFrontiers == null) {
            this.dominanceFrontiers = new Multimap<BasicBlock>();

            getDominatorTree(); // touch the dominator tree

            for (BasicBlock x : reverseTopologicalTraversal()) {
                Set<BasicBlock> dfx = this.dominanceFrontiers.get(x);

                // Compute DF(local)
                for (BasicBlock y : x.getSuccessors()) {
                    if (idom.get(y) != x) {
                        dfx.add(y);
                    }
                }

                // Compute DF(up)
                for (BasicBlock z : this.dominatorTree.get(x)) {
                    for (BasicBlock y : this.dominanceFrontiers.get(z)) {
                        if (idom.get(y) != x) {
                            dfx.add(y);
                        }
                    }
                }
            }
        }

        return this.dominanceFrontiers;
    }

    /**
     * Create and/or fetch a topological traversal of the dominator tree, such
     * that for every node, idom(node) appears before node.
     *
     * @return the topological traversal of the dominator tree, as an immutable List.
     */
    public List<BasicBlock> topologicalTraversal() {
        return Collections.unmodifiableList(getToplogicalTraversalImplementation());
    }

    /**
     * Create and/or fetch a reverse topological traversal of the dominator
     * tree, such that for every node, node appears before idom(node).
     *
     * @return a reverse topological traversal of the dominator tree, as an
     * immutable List.
     */
    public Iterable<BasicBlock> reverseTopologicalTraversal() {
        return new Iterable<BasicBlock>() {
            @Override
            public Iterator<BasicBlock> iterator() {
                return getToplogicalTraversalImplementation().descendingIterator();
            }
        };
    }

    /**
     * Depth-first search the graph and initialize data structures.
     *
     * @param root The root of the graph.
     */
    private void depthFirstSearch(ControlFlowGraph controlFlowGraph) {
        Iterator<BasicBlock> it = new DepthFirstPreorderIterator(controlFlowGraph);

        while (it.hasNext()) {
            BasicBlock node = it.next();

            if (!semi.containsKey(node)) {
                vertex.add(node);

                // Initial assumption: the node's semidominator is itself.
                semi.put(node, semi.size());
                label.put(node, node);

                for (BasicBlock child : node.getSuccessors()) {
                    pred.get(child).add(node);
                    if (!semi.containsKey(child)) {
                        parent.put(child, node);
                    }
                }
            }
        }
    }

    /**
     * Steps 2, 3, and 4 of Lengauer-Tarjan.
     */
    private void computeDominators() {
        int lastSemiNumber = semi.size() - 1;

        for (int i = lastSemiNumber; i > 0; i--) {
            BasicBlock w = vertex.get(i);
            BasicBlock p = this.parent.get(w);

            // step 2: compute semidominators
            // for each v in pred(w)...
            int semidominator = semi.get(w);
            for (BasicBlock v : pred.get(w)) {
                semidominator = Math.min(semidominator, semi.get(eval(v)));
            }

            semi.put(w, semidominator);
            bucket.get(vertex.get(semidominator)).add(w);

            // Link w into the forest via its parent, p
            link(p, w);

            // step 3: implicitly compute idominators
            // for each v in bucket(parent(w)) ...
            for (BasicBlock v : bucket.get(p)) {
                BasicBlock u = eval(v);

                if (semi.get(u) < semi.get(v)) {
                    idom.put(v, u);
                } else {
                    idom.put(v, p);
                }
            }

            bucket.get(p).clear();
        }

        // step 4: explicitly compute idominators
        for (int i = 1; i <= lastSemiNumber; i++) {
            BasicBlock w = vertex.get(i);

            if (idom.get(w) != vertex.get((semi.get(w)))) {
                idom.put(w, idom.get(idom.get(w)));
            }
        }
    }

    /**
     * Extract the node with the least-numbered semidominator in the (processed)
     * ancestors of the given node.
     *
     * @param v The node of interest.
     * @return "If v is the root of a tree in the forest, return v. Otherwise,
     * let r be the root of the tree which contains v. Return any vertex
     * u != r of miniumum semi(u) on the path r-*v."
     */
    private BasicBlock eval(BasicBlock v) {
        // This version of Lengauer-Tarjan implements
        // eval(v) as a path-compression procedure.
        compress(v);
        return label.get(v);
    }

    /**
     * Traverse ancestor pointers back to a subtree root, then propagate the
     * least semidominator seen along this path through the "label" map.
     */
    private void compress(BasicBlock v) {
        Stack<BasicBlock> worklist = new Stack<BasicBlock>();
        worklist.add(v);

        BasicBlock a = this.ancestor.get(v);

        // Traverse back to the subtree root.
        while (this.ancestor.containsKey(a)) {
            worklist.push(a);
            a = this.ancestor.get(a);
        }

        // Propagate semidominator information forward.
        BasicBlock ancestor = worklist.pop();
        int leastSemi = semi.get(label.get(ancestor));

        while (!worklist.empty()) {
            BasicBlock descendent = worklist.pop();
            int currentSemi = semi.get(label.get(descendent));

            if (currentSemi > leastSemi) {
                label.put(descendent, label.get(ancestor));
            } else {
                leastSemi = currentSemi;
            }

            // Prepare to process the next iteration.
            ancestor = descendent;
        }
    }

    /**
     * Simple version of link(parent,child) simply links the child into the
     * parent's forest, with no attempt to balance the subtrees or otherwise
     * optimize searching.
     */
    private void link(BasicBlock parent, BasicBlock child) {
        this.ancestor.put(child, parent);
    }

    /**
     * Create/fetch the topological traversal of the dominator tree.
     *
     * @return {@link this.topologicalTraversal}, the traversal of the dominator
     * tree such that for any node n with a dominator, n appears before
     * idom(n).
     */
    private LinkedList<BasicBlock> getToplogicalTraversalImplementation() {
        if (this.topologicalTraversalImpl == null) {
            this.topologicalTraversalImpl = new LinkedList<BasicBlock>();

            for (BasicBlock node : this.vertex) {
                int idx = this.topologicalTraversalImpl.indexOf(this.idom.get(node));

                if (idx != -1) {
                    this.topologicalTraversalImpl.add(idx + 1, node);
                } else {
                    this.topologicalTraversalImpl.add(node);
                }
            }
        }

        return this.topologicalTraversalImpl;
    }

    /**
     * Multimap maps a key to a set of values.
     */
    @SuppressWarnings("serial")
    public static class Multimap<T> extends HashMap<T, Set<T>> {

        /**
         * Fetch the set for a given key, creating it if necessary.
         *
         * @param key - the key.
         * @return the set of values mapped to the key.
         */
        @SuppressWarnings("unchecked")
        @Override
        public Set<T> get(Object key) {
            if (!this.containsKey(key)) {
                this.put((T) key, new HashSet<T>());
            }
            return super.get(key);
        }
    }
}
