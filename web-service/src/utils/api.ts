//import { Paths } from "v-network-graph";

import axios from "axios";

export const remoteFetchOptions = (
  keyword: string,
  params: { isLoading: boolean; options: any; [key: string]: any }
) => {
  if (keyword) {
    params.isLoading = true;

    axios({
      method: "post",
      url: "api/esscd/filter",
      headers: { "content-type": "application/json" },
      data: { keyword, filterType: params.filterType },
    })
      .then((jsonResponse) => {
        const results = jsonResponse.data.results;
        params.options = results.map((result: string) => {
          return { key: result, label: result, value: result };
        });
      })
      .then(() => {
        params.isLoading = false;
      });
  } else {
    params.options = [];
  }
};

export const remoteFetchSystemDiagram = (params: any, diagramData: any) => {
  if (params.filterLevel === "all") {
    axios({
      method: "get",
      url: "api/esscd/diagram",
      headers: { "content-type": "application/json" },
    }).then((jsonResponse) => {
      diagramData.value = jsonResponse.data;
    });
    return;
  } else if (params.selectedValues.length === 0) {
    diagramData.value = undefined;
    return;
  }

  const selectedValues: string[] = params.selectedValues; // mostly they are strings

  axios({
    method: "post",
    url: "api/esscd/diagram",
    headers: { "content-type": "application/json" },
    data: {
      selectedValues: selectedValues,
      filterType: params.filterType,
      filterLevel: params.filterLevel,
    },
  }).then((jsonResponse) => {
    diagramData.value = jsonResponse.data;
  });
};

// THIS IS FOR SHORTEST PATH SEARCHING //////////////////////////////
//   } else if (
//     searchParams.method == "shortest-path" &&
//     selectedSids.length == 2
//   ) {
//     fetch("api/esscd/path", {
//       method: "post",
//       headers: { "content-type": "application/json" },
//       body: JSON.stringify({ sids: selectedSids }),
//     })
//       .then((response) => response.json())
//       .then((jsonResponse) => {
//         console.log(jsonResponse);
//         let newPaths: Paths = [];
//         for (const path of jsonResponse.paths) {
//           let newPath: Path = { edges: [] };
//           for (const edge of path) {
//             const edgeName = `${edge.sourceSid}-${edge.targetSid}`;
//             newPath.edges.push(edgeName);
//           }
//           newPaths.push(newPath);
//         }
//         return newPaths;
//       })
//       .then((newPaths: Paths) => {
//         Object.assign(paths, newPaths);
//       });
////////////////////////////////////////////////////////////////////
