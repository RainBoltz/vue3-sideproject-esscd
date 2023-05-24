//import { Paths } from "v-network-graph";

export const remoteFetchOptions = (
  keyword: string,
  params: { isLoading: boolean; options: any; [key: string]: any }
) => {
  if (keyword) {
    params.isLoading = true;

    fetch("api/esscd/filter", {
      method: "post",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ keyword, filterType: params.filterType }),
    })
      .then((response) => response.json())
      .then((jsonResponse) => {
        const results = jsonResponse.results;
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
    fetch("api/esscd/diagram", {
      method: "get",
      headers: { "content-type": "application/json" },
    })
      .then((response) => response.json())
      .then((jsonResponse) => {
        diagramData.value = jsonResponse;
      });
    return;
  } else if (params.selectedValues.length === 0) {
    diagramData.value = undefined;
    return;
  }

  const selectedValues: string[] = params.selectedValues; // mostly they are strings

  fetch("api/esscd/diagram", {
    method: "post",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      selectedValues: selectedValues,
      filterType: params.filterType,
      filterLevel: params.filterLevel,
    }),
  })
    .then((response) => response.json())
    .then((jsonResponse) => {
      diagramData.value = jsonResponse;
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
