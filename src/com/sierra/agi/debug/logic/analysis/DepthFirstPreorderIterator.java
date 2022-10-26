package com.sierra.agi.debug.logic.analysis;

import java.util.*;

/**
 * Implements a depth first pre-order Iterator implementation for the given ControlFlowGraph.
 */
public class DepthFirstPreorderIterator implements Iterator<BasicBlock> {

    /**
     * The Stack of BasicBlocks still to be visited.
     */
    private final Stack<BasicBlock> toBeVisited;

    /**
     * The Set of edges already visited.
     */
    private final Set<Edge> visitedEdges;

    /**
     * Constructor for DepthFirstPreorderIterator.
     *
     * @param controlFlowGraph The ControlFlowGraph to create a depth first preorder Iterator for.
     */
    public DepthFirstPreorderIterator(ControlFlowGraph controlFlowGraph) {
        this.toBeVisited = new Stack<BasicBlock>();
        this.visitedEdges = new HashSet<Edge>();

        // We start with the first BasicBlock in the ControlFlowGraph.
        this.toBeVisited.add(controlFlowGraph.getStartBlock());
    }

    /**
     * Returns true if the Iterator has more BasicBlock elements to traverse.
     *
     * @return true if the Iterator has more BasicBlock elements to traverse.
     */
    @Override
    public boolean hasNext() {
        return !toBeVisited.isEmpty();
    }

    /**
     * Returns the next BasicBlock in the depth first preorder iteration.
     *
     * @return The next BasicBlock in the depth first preorder iteration.
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public BasicBlock next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        BasicBlock next = toBeVisited.pop();
        pushSuccessors(next);
        return next;
    }

    /**
     * Traverse any previously unvisited edges by adding the destination block
     * to the toBeVisited Stack.
     *
     * @param block The current block.
     */
    private void pushSuccessors(BasicBlock block) {
        for (BasicBlock successor : block.getSuccessors()) {
            Edge edge = new Edge(block, successor);
            if (!visitedEdges.contains(edge)) {
                visitedEdges.add(edge);
                toBeVisited.push(successor);
            }
        }
    }

    /**
     * Would normally remove from the underlying collection the last element returned
     * by this Iterator, but as this is an optional operation, it hasn't been implemented.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * The Edge class is used to detect edges previously traversed, by implementing
     * the hashCode and equals operations, and being used as keys in the visitedEdges
     * HashSet.
     */
    private class Edge {

        /**
         * The BasicBlock that the Edge is from.
         */
        private final BasicBlock from;

        /**
         * The BasicBlock that the Edge is to.
         */
        private final BasicBlock to;

        /**
         * Constructor for Edge.
         *
         * @param from The BasicBlock that the Edge is from.
         * @param to   The BasicBlock that the Edge is to.
         */
        Edge(BasicBlock from, BasicBlock to) {
            this.from = from;
            this.to = to;
        }

        /**
         * Generates a composite hash code using the two vertices so that an Edge can
         * be used in the visitedEdges HashSet.
         *
         * @return The composite hash code of the from/to vertices.
         */
        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 17 + from.hashCode();
            hash = hash * 31 + to.hashCode();
            return hash;
        }

        /**
         * Use the two vertices to determine equality of an Edge so it can be used
         * in the visitedEdges HashSet.
         *
         * @param other The other object to compare.
         * @return true if other is an Edge, and both Edges' from/to vertices match.
         */
        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;

            } else if (other instanceof Edge otherEdge) {
                return (from == otherEdge.from) && (to == otherEdge.to);
            }

            return false;
        }
    }
}
