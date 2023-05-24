import { Nodes, Edges, Layouts } from "v-network-graph";
import { normalNodeStyle, normalEdgeStyle } from "@/utils/styles";

export const buildDiagram = (nodes: Nodes, edges: Edges, apiResponse: any) => {
  Object.keys(nodes).forEach((id) => delete nodes[id]);
  Object.keys(edges).forEach((id) => delete edges[id]);

  // nodes
  const newNodes: Nodes = {};
  for (const system of apiResponse.systems) {
    newNodes[`${system.sid}`] = {
      sid: system.sid,
      alias: system.alias,
      name: system.name,
      type: system.type,
      os: system.os,
      lang: system.lang,
      ...normalNodeStyle,
    };
  }

  // edges
  const newEdges: Edges = {};
  for (const relationship of apiResponse.relationships) {
    newEdges[`${relationship.sourceSid}-${relationship.targetSid}`] = {
      protocol: relationship.protocol,
      description: relationship.description,
      source: `${relationship.sourceSid}`,
      target: `${relationship.targetSid}`,
      ...normalEdgeStyle,
    };
  }

  Object.assign(nodes, newNodes);
  Object.assign(edges, newEdges);
};

export const orderLayouts = (
  blockSize: number,
  svgCanvasStartPoint: { x: number; y: number },
  svgCanvasEndPoint: { x: number; y: number },
  nodeIds: string[],
  layouts: Layouts
) => {
  const svgCanvasHeight = svgCanvasEndPoint.y - svgCanvasStartPoint.y;
  const svgCanvasWidth = svgCanvasEndPoint.x - svgCanvasStartPoint.x;
  const verticalBlocks = Math.floor(svgCanvasHeight / blockSize);
  const horizontalBlocks = Math.floor(svgCanvasWidth / blockSize);
  const defaultOffsetBlocks = 1; // TODO: make this configurable
  const nodeHorizontalBlocks = Math.ceil(Math.sqrt(nodeIds.length));
  const offsetTopBlocks =
    nodeHorizontalBlocks < verticalBlocks - 2 * defaultOffsetBlocks
      ? Math.ceil(
          (verticalBlocks - nodeHorizontalBlocks - 2 * defaultOffsetBlocks) / 2
        )
      : 0;
  const offsetLeftBlocks =
    nodeHorizontalBlocks < horizontalBlocks - 2 * defaultOffsetBlocks
      ? Math.ceil(
          (horizontalBlocks - nodeHorizontalBlocks - 2 * defaultOffsetBlocks) /
            2
        )
      : 0;

  const nodePositions: { [k: string]: { x: number; y: number } } = {};
  for (
    let iVertical = 0;
    nodeHorizontalBlocks * iVertical < nodeIds.length;
    iVertical++
  ) {
    for (
      let iHorizontal = 0;
      iHorizontal < nodeHorizontalBlocks;
      iHorizontal++
    ) {
      const nodeId = nodeIds[nodeHorizontalBlocks * iVertical + iHorizontal];
      nodePositions[nodeId] = {
        x:
          svgCanvasStartPoint.x +
          blockSize * (iHorizontal + offsetLeftBlocks + defaultOffsetBlocks),
        y:
          svgCanvasStartPoint.y +
          blockSize * (iVertical + offsetTopBlocks + defaultOffsetBlocks),
      };
    }
  }

  Object.assign(layouts.nodes, nodePositions);
};
