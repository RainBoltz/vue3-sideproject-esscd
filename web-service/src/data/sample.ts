import { Nodes, Edges } from "v-network-graph";

const nodes: Nodes = {
  node1: { name: "N1" },
  node2: { name: "N2" },
  node3: { name: "N3" },
};

const edges: Edges = {
  edge1: { source: "node1", target: "node2" },
  edge8: { source: "node2", target: "node3" },
  edge9: { source: "node2", target: "node3" },
  edge10: { source: "node3", target: "node1" },
};

export default {
  nodes,
  edges,
};
