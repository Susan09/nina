/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2009, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* -------------------------
 * GraphUnion.java
 * -------------------------
 * (C) Copyright 2009-2009, by Ilya Razenshteyn
 *
 * Original Author:  Ilya Razenshteyn and Contributors.
 *
 * $Id: GraphUnion.java 695 2009-07-06 02:18:35Z perfecthash $
 *
 * Changes
 * -------
 * 02-Feb-2009 : Initial revision (IR);
 *
 */
package edu.nd.nina.graph;

import java.io.*;

import java.util.*;

import edu.nd.nina.*;
import edu.nd.nina.util.*;


/**
 * <p>Read-only union of two graphs: G<sub>1</sub> and G<sub>2</sub>. If
 * G<sub>1</sub> = (V<sub>1</sub>, E<sub>1</sub>) and G<sub>2</sub> =
 * (V<sub>2</sub>, E<sub>2</sub>) then their union G = (V, E), where V is the
 * union of V<sub>1</sub> and V<sub>2</sub>, and E is the union of E<sub>1</sub>
 * and E<sub>1</sub>.</p>
 *
 * <p><tt>GraphUnion</tt> implements <tt>Graph</tt> interface. <tt>
 * GraphUnion</tt> uses <tt>WeightCombiner</tt> to choose policy for calculating
 * edge weight.</p>
 */
public class GraphUnion<V, E, G extends Graph<V, E>>
    extends AbstractGraph<V, E>
    implements Serializable
{
    //~ Static fields/initializers ---------------------------------------------

    private static final long serialVersionUID = -740199233080172450L;

    private static final String READ_ONLY = "union of graphs is read-only";

    //~ Instance fields --------------------------------------------------------

    private G g1;
    private G g2;
    private WeightCombiner operator;

    //~ Constructors -----------------------------------------------------------

    public GraphUnion(G g1, G g2, WeightCombiner operator)
    {
        if (g1 == null) {
            throw new NullPointerException("g1 is null");
        }
        if (g2 == null) {
            throw new NullPointerException("g2 is null");
        }
        if (g1 == g2) {
            throw new IllegalArgumentException("g1 is equal to g2");
        }
        this.g1 = g1;
        this.g2 = g2;
        this.operator = operator;
    }

    public GraphUnion(G g1, G g2)
    {
        this(g1, g2, WeightCombiner.SUM);
    }

    //~ Methods ----------------------------------------------------------------

    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> res = new HashSet<E>();
        if (g1.containsVertex(sourceVertex)
            && g1.containsVertex(targetVertex))
        {
            res.addAll(g1.getAllEdges(sourceVertex, targetVertex));
        }
        if (g2.containsVertex(sourceVertex)
            && g2.containsVertex(targetVertex))
        {
            res.addAll(g2.getAllEdges(sourceVertex, targetVertex));
        }
        return Collections.unmodifiableSet(res);
    }

    public E getEdge(V sourceVertex, V targetVertex)
    {
        E res = null;
        if (g1.containsVertex(sourceVertex)
            && g1.containsVertex(targetVertex))
        {
            res = g1.getEdge(sourceVertex, targetVertex);
        }
        if ((res == null)
            && g2.containsVertex(sourceVertex)
            && g2.containsVertex(targetVertex))
        {
            res = g2.getEdge(sourceVertex, targetVertex);
        }
        return res;
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public EdgeFactory<V, E> getEdgeFactory()
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public boolean addVertex(V v)
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }
    
    /**
	 * @see Graph#getAllMatchingType(Class<?>)
	 */
	public Set<V> getAllMatchingType(Class<?> clazz) {
		Set<V> res = new HashSet<V>();
		res.addAll(g1.getAllMatchingType(clazz));
		res.addAll(g2.getAllMatchingType(clazz));

		return Collections.unmodifiableSet(res);
	}
	
    /**
	 * @see Graph#getTypes()
	 */
	public Set<Class<?>> getTypes() {
		Set<Class<?>> res = new HashSet<Class<?>>();
		res.addAll(g1.getTypes());
		res.addAll(g2.getTypes());

		return Collections.unmodifiableSet(res);
	}

    public boolean containsEdge(E e)
    {
        return g1.containsEdge(e) || g2.containsEdge(e);
    }

    public boolean containsVertex(V v)
    {
        return g1.containsVertex(v) || g2.containsVertex(v);
    }

    public Set<E> edgeSet()
    {
        Set<E> res = new HashSet<E>();
        res.addAll(g1.edgeSet());
        res.addAll(g2.edgeSet());
        return Collections.unmodifiableSet(res);
    }

    public Set<E> edgesOf(V vertex)
    {
        Set<E> res = new HashSet<E>();
        if (g1.containsVertex(vertex)) {
            res.addAll(g1.edgesOf(vertex));
        }
        if (g2.containsVertex(vertex)) {
            res.addAll(g2.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public boolean removeEdge(E e)
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>, because <tt>
     * GraphUnion</tt> is read-only.
     */
    public boolean removeVertex(V v)
    {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    public Set<V> vertexSet()
    {
        Set<V> res = new HashSet<V>();
        res.addAll(g1.vertexSet());
        res.addAll(g2.vertexSet());
        return Collections.unmodifiableSet(res);
    }

    public V getEdgeSource(E e)
    {
        if (g1.containsEdge(e)) {
            return g1.getEdgeSource(e);
        }
        if (g2.containsEdge(e)) {
            return g2.getEdgeSource(e);
        }
        return null;
    }

    public V getEdgeTarget(E e)
    {
        if (g1.containsEdge(e)) {
            return g1.getEdgeTarget(e);
        }
        if (g2.containsEdge(e)) {
            return g2.getEdgeTarget(e);
        }
        return null;
    }

    public double getEdgeWeight(E e)
    {
        if (g1.containsEdge(e) && g2.containsEdge(e)) {
            return operator.combine(g1.getEdgeWeight(e), g2.getEdgeWeight(e));
        }
        if (g1.containsEdge(e)) {
            return g1.getEdgeWeight(e);
        }
        if (g2.containsEdge(e)) {
            return g2.getEdgeWeight(e);
        }
        throw new IllegalArgumentException("no such edge in the union");
    }

    /**
     * @return G<sub>1</sub>
     */
    public G getG1()
    {
        return g1;
    }

    /**
     * @return G<sub>2</sub>
     */
    public G getG2()
    {
        return g2;
    }
}

// End GraphUnion.java
