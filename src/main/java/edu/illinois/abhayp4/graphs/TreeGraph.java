package edu.illinois.abhayp4.graphs;

sealed abstract class TreeGraph<T> extends Graph<T> permits PerfectTreeGraph, FractalTreeGraph {

}