/**
 * TreeGraph.java
 * @author Abhay Pokhriyal
 */

package edu.illinois.abhayp4.brain_project.graphs;

sealed abstract class TreeGraph<T> extends Graph<T> permits PerfectTreeGraph, FractalTreeGraph {

}