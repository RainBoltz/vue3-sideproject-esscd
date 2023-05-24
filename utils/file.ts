import { Nodes } from "v-network-graph";

export const downloadExportCsv = (
  nodes: Nodes,
  selectedNodes: string[],
  highlightNodes: string[],
  exportNeighbors: boolean
) => {
  let outputNodes: string[];
  if (selectedNodes.length === 0) {
    outputNodes = Object.keys(nodes);
  } else {
    if (exportNeighbors) {
      outputNodes = selectedNodes.concat(highlightNodes);
    } else {
      outputNodes = selectedNodes;
    }
  }

  const headers = ["sid", "name", "alias", "type", "os", "lang"];
  const rows = [["代碼", "全名", "簡稱", "類別", "作業系統", "開發語言"]];

  for (const nodeId of outputNodes) {
    rows.push(headers.map((header) => nodes[nodeId][header]));
  }

  const csvContent = rows.map((e) => e.join(",")).join("\n");
  const url = URL.createObjectURL(new Blob([csvContent], { type: "text/csv" }));
  const a = document.createElement("a");
  a.href = url;
  a.download = "E.Sun Bank SCD.csv";
  a.click();
  window.URL.revokeObjectURL(url);
};

export const downloadExportSvg = (graph: any) => {
  if (!graph) return;

  const text = graph.getAsSvg() ?? "";
  const url = URL.createObjectURL(new Blob([text], { type: "octet/stream" }));
  const a = document.createElement("a");
  a.href = url;
  a.download = "E.Sun Bank SCD graph.svg";
  a.click();
  window.URL.revokeObjectURL(url);
};
