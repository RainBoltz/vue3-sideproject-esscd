<template>
  <div id="scd-control-panel" class="control-panel" :class="{'control-panel-closed': isPanelCollapsed}" style="z-index: 1">
    <el-button
      class="collapse-btn"
      @click="panelCollapseSwitch"
      circle
    >{{ isPanelCollapsed ? '+' : '-' }}</el-button>
    <el-tabs type="border-card" class="stretch-to-max">
      <el-tab-pane label="子圖搜尋">
        <el-row :gutter="10" class="control" style="margin-top: 0px">
          <el-col :span="10">
            <el-select
              v-model="subgraphVars.selectedValues"
              remote
              filterable
              :remote-method="getOptions"
              :loading="subgraphVars.isLoading"
              no-match-text="沒有符合的選項"
              no-data-text="沒有選項"
              placeholder="請輸入搜尋條件"
              loading-text="載入中..."
              size="large"
              multiple
              clearable
              collapse-tags
              reserve-keyword
              value-key="sid"
              class="el-select-filter"
            >
              <el-option
                v-for="item in subgraphVars.options"
                :key="item.key"
                :label="item.label"
                :value="item.value"
              >
                <span style="float: left">{{ item.value[subgraphVars.filterType] ?? item.value }}</span>
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="7">
            <el-select v-model="subgraphVars.filterType" size="large">
              <el-option label="資訊資產代碼" value="sid"></el-option>
              <el-option label="資訊資產名稱" value="name"></el-option>
              <el-option label="資訊資產簡稱" value="alias"></el-option>
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-select v-model="subgraphVars.filterLevel" size="large">
              <el-option label="1層" value="1"></el-option>
              <el-option label="2層" value="2"></el-option>
              <el-option label="3層" value="3"></el-option>
              <el-option label="全部" value="all"></el-option>
            </el-select>
          </el-col>
          <el-col :span="3">
            <el-button @click="getDiagram" size="large" color="#00a19b" style="color: white">搜尋</el-button>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="顯示">
        <el-row :gutter="15" class="control" style="margin-top: -8px">
          <el-col :span="8">
            <el-select v-model="configNodeLabelText" size="large">
              <el-option key="sid" label="資訊資產代碼" value="sid" />
              <el-option key="alias" label="資訊資產簡稱" value="alias" />
              <el-option key="name" label="資訊資產名稱" value="name" />
              <el-option key="type" label="系統型別" value="type" />
              <el-option key="os" label="作業系統" value="os" />
              <el-option key="lang" label="程式語言" value="lang" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-checkbox v-model="configNodeLabelVisible" label="顯示標籤" size="large"></el-checkbox>
          </el-col>
          <el-col :span="8"></el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="調整">
        <el-row :gutter="15" class="control" style="margin-top: -8px">
          <el-col :span="6">
            <el-button
              @click="graph?.panToCenter(); graph?.fitToContents();"
              size="large"
              plain
              color="#87d4c7"
              style="color: #00a19b"
            >縮放置中</el-button>
          </el-col>
          <el-col :span="6">
            <el-button
              @click="resetLayouts"
              size="large"
              plain
              color="#87d4c7"
              style="color: #00a19b"
            >自動排序</el-button>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="configViewLayoutHandler" label="動態節點" size="large"></el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="configViewScalingObjects" label="自由節點大小" size="large"></el-checkbox>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="輸出">
        <el-row :gutter="15" class="control" style="margin-top: -8px">
          <el-col :span="5">
            <el-button
              @click="() => downloadExportCsv(nodes, selectedNodes, highlightNodes, exportNeighbors)"
              size="large"
              plain
              color="#87d4c7"
              style="color: #00a19b"
            >輸出csv</el-button>
          </el-col>
          <el-col :span="8">
            <el-checkbox v-model="exportNeighbors" label="包含鄰節點" size="large"></el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-button
              @click="() => downloadExportSvg(graph)"
              size="large"
              plain
              color="#87d4c7"
              style="color: #00a19b"
            >輸出全圖svg</el-button>
          </el-col>
          <el-col :span="5"></el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>

  <el-empty
    v-if="!diagramData"
    description="歡迎來到全行系統關聯情報平台"
    style="z-index: 0; position: relative; height: 100vh; width: 100%; --el-font-size-base: 24px;"
    :image="esunLogoURL"
    :image-size="200"
  ></el-empty>
  <div class="tooltip-wrapper">
    <v-network-graph
      v-model:selected-nodes="selectedNodes"
      v-model:selected-edges="selectedEdges"
      v-model:layouts="layouts"
      :nodes="nodes"
      :edges="edges"
      :paths="paths"
      :configs="configs"
      :layers="layers"
      :event-handlers="eventHandlers"
      ref="graph"
      style="z-index: 0"
    >
      <template #override-node="{ nodeId, scale, config, ...slotProps }">
        <circle :r="config.radius * scale" :fill="config.color" v-bind="slotProps" />
        <!-- Use v-html to interpret escape sequences for icon characters. -->
        <text
          class="material-icons"
          font-family="Material Icons"
          :font-size="22 * scale"
          fill="#ffffff"
          text-anchor="middle"
          dominant-baseline="central"
          style="pointer-events: none"
          v-html="typeToIconCodePoint(nodes[nodeId].type)"
        />
      </template>
    </v-network-graph>
    <div
      ref="tooltip"
      class="tooltip"
      :style="{ ...tooltipPos, opacity: tooltipVars.opacity, visibility: tooltipVars.visibility }"
    >
      <span v-if="tooltipVars.targetType == 'node'">
        <div>代號: {{ nodes[tooltipVars.targetId]?.sid ?? "" }}</div>
        <div>全名: {{ nodes[tooltipVars.targetId]?.name ?? "" }}</div>
        <div>簡稱: {{ nodes[tooltipVars.targetId]?.alias ?? "" }}</div>
        <div>系統型別: {{ nodes[tooltipVars.targetId]?.type ?? "" }}</div>
        <div>作業系統: {{ nodes[tooltipVars.targetId]?.os ?? "" }}</div>
        <div>程式語言: {{ nodes[tooltipVars.targetId]?.lang ?? "" }}</div>
      </span>
      <span v-if="tooltipVars.targetType == 'edge'">
        <div>傳遞方法: {{ edges[tooltipVars.targetId]?.protocol ?? "" }}</div>
        <div>詳細資料: {{ edges[tooltipVars.targetId]?.description ?? "" }}</div>
        <div>來源: {{ edges[tooltipVars.targetId]?.source ?? "" }}</div>
        <div>對象: {{ edges[tooltipVars.targetId]?.target ?? "" }}</div>
      </span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, watch, computed } from "vue"
import { Nodes, Edges, UserConfigs, Layers, VNetworkGraphInstance, SimpleLayout, Paths, Layouts, EventHandlers } from "v-network-graph"
import { ForceLayout, ForceEdgeDatum, ForceNodeDatum } from "v-network-graph/lib/force-layout"
import { buildDiagram, orderLayouts } from "@/utils/graph"
import { remoteFetchOptions, remoteFetchSystemDiagram } from "@/utils/api"
import { typeToIconCodePoint } from "@/utils/convert"
import { normalNodeStyle, normalEdgeStyle, highlightedNodeStyle, highlightedEdgeStyle, otherEdgeStyle, otherNodeStyle } from "@/utils/styles"
import { downloadExportCsv, downloadExportSvg } from "@/utils/file"
import Colors from '@/styles/main'

// for graph
const nodes: Nodes = reactive({})
const edges: Edges = reactive({})
const paths: Paths = reactive<Paths>([])
const layouts: Layouts = reactive<Layouts>({ nodes: {} })
const layers: Layers = { "font-defs": "root", }
const configs: UserConfigs = reactive({
  view: {
    scalingObjects: true,
    layoutHandler: new ForceLayout({
      positionFixedByDrag: true,
      positionFixedByClickWithAltKey: true,
    }),
  },
  node: {
    selectable: true,
    normal: {
      color: node => node.color,
      radius: node => node.radius,
    },
    hover: {
      radius: 25,
      color: Colors.default.BUTTON_COLOR,
    },
    label: {
      visible: true,
      text: "name",
      color: node => node.labelColor,
      background: {
        visible: true,
        color: Colors.BORDER_COLORS[1] + "bb",
        padding: {
          vertical: 1,
          horizontal: 4,
        },
        borderRadius: 8
      },
    },
    zOrder: {
      enabled: true,
      zIndex: (node: any) => node.zIndex,
      bringToFrontOnHover: true,
      bringToFrontOnSelected: true,
    }
  },
  edge: {
    selectable: false,
    normal: {
      width: edge => edge.width,
      color: edge => edge.color,
      dasharray: edge => edge.dasharray,
      linecap: edge => edge.linecap,
      animate: edge => edge.animate,
      animationSpeed: edge => edge.animationSpeed,
    },
    hover: {
      width: edge => edge.width + 2,
      color: Colors.PRIMARY_COLORS[9], //work-around
      dasharray: edge => edge.dasharray,
      linecap: edge => edge.linecap,
      animate: edge => edge.animate,
      animationSpeed: edge => edge.animationSpeed,
    },
    selected: {  //work-around
      width: highlightedEdgeStyle.width,
      color: highlightedEdgeStyle.color,
      dasharray: highlightedEdgeStyle.dasharray,
      linecap: highlightedEdgeStyle.linecap as any,
      animate: highlightedEdgeStyle.animate,
      animationSpeed: highlightedEdgeStyle.animationSpeed,
    },
    gap: 5,
    type: "straight",
    margin: 2,
    marker: {
      source: {
        type: "none",
        width: 4,
        height: 4,
        margin: -1,
        units: "strokeWidth",
        color: null
      },
      target: {
        type: "arrow",
        width: 4,
        height: 4,
        margin: -1,
        units: "strokeWidth",
        color: null
      },
    },
  },
  path: {
    visible: true,
    path: {
      width: 5,
      dasharray: "10 16",
      animate: true,
      animationSpeed: 40
    },
  },
})

// graph hooks
const diagramData = ref<any>({})
const graph = ref<VNetworkGraphInstance>()

// tooltips
const tooltipVars = reactive<{
  targetType: "node" | "edge" | "", targetId: string, opacity: number, visibility: "hidden" | "visible"
}>({
  targetType: "", targetId: "", opacity: 0, visibility: "hidden"
})
const tooltip = ref<HTMLDivElement>()

// interfaces
interface INodeProps {
  sid: string,
  alias: string,
  name: string,
  type: string,
  [key: string]: any
}
interface IOption {
  key: string,
  value: INodeProps,
  label: string
}
interface IVars {
  isLoading: boolean,
  options: IOption[],
  [key: string]: any
}

// subgraph related
const subgraphVars = reactive<IVars>({
  selectedValues: [],//["TS0108", "TS0094", "UP0029"], ODS可以三層
  isLoading: false,
  options: [],
  filterType: "sid",
  filterLevel: "1",
})
const getOptions = (query: string) => {
  remoteFetchOptions(query, subgraphVars)
}
const getDiagram = () => {
  remoteFetchSystemDiagram(subgraphVars, diagramData)
}

// filtering related
const selectedNodes = ref<string[]>([])
const selectedHighlightNodeIdMapping = ref<{ [k: string]: string }>({}) //nodeIds, used for highlight the selectedValues
const selectedEdges = ref<string[]>([])
const highlightNodes = ref<string[]>([])
const highlightEdges = ref<string[]>([])

// export configs
const exportNeighbors = ref<boolean>(true)

// control panel collapse function
const isPanelCollapsed = ref<boolean>(false)
const panelCollapseSwitch = () => {
  isPanelCollapsed.value = !isPanelCollapsed.value
}

// lifecycle: on mount
onMounted(() => {
  getDiagram() // no selection will return whole graph
})

// graph event handlers
const eventHandlers: EventHandlers = {
  "node:pointerover": ({ node }) => {
    tooltipVars.targetType = "node"
    tooltipVars.targetId = node
    tooltipVars.opacity = 1
    tooltipVars.visibility = "visible"
    console.log(layouts.nodes[node])
  },
  "node:pointerout": () => {
    tooltipVars.opacity = 0
    tooltipVars.visibility = "hidden"
    tooltipVars.targetId = ""
  },
  "edge:pointerover": ({ edge, edges }) => {
    tooltipVars.targetType = "edge"
    tooltipVars.targetId = edge ?? edges[0]
    tooltipVars.opacity = 1
    tooltipVars.visibility = "visible"
  },
  "edge:pointerout": () => {
    tooltipVars.opacity = 0
    tooltipVars.visibility = "hidden"
    tooltipVars.targetId = ""
  }
}

// layout ordering function
const resetLayouts = () => {
  const domStartPoint = { x: 0, y: 0 }
  const domEndPoint = { x: window.innerWidth, y: window.innerHeight }
  const svgCanvasStartPoint = graph.value?.translateFromDomToSvgCoordinates(domStartPoint) ?? { x: 0, y: 0 }
  const svgCanvasEndPoint = graph.value?.translateFromDomToSvgCoordinates(domEndPoint) ?? { x: 0, y: 0 }

  console.log(domStartPoint, domEndPoint, svgCanvasStartPoint, svgCanvasEndPoint)

  orderLayouts(100, svgCanvasStartPoint, svgCanvasEndPoint, Object.keys(nodes), layouts)
}

// composition api: watch
watch(diagramData, () => {
  if (diagramData.value) {
    buildDiagram(nodes, edges, diagramData.value)
  }

  resetLayouts()

  // workaround for color of selected nodes in select
  const nodeIds = Object.keys(nodes)
  subgraphVars.selectedValues.forEach((value: string) => {
    let nodeId: string | undefined = selectedHighlightNodeIdMapping.value[value]
    if (nodeId === undefined) {
      switch (subgraphVars.filterType) {
        case 'sid': nodeId = value; break;
        case 'alias': nodeId = nodeIds.find(_nodeId => nodes[_nodeId].alias === value); break;
        case 'name': nodeId = nodeIds.find(_nodeId => nodes[_nodeId].name === value); break;
        default: nodeId = undefined;
      }
    }
    if (nodeId) {
      selectedHighlightNodeIdMapping.value[value] = nodeId
      if (nodeIds.includes(nodeId))
        nodes[nodeId].color = Colors.default.WARNING_COLOR + '99'
    }
  })

  // pan to first node of the selected nodes
  if (graph.value && subgraphVars.selectedValues.length > 0) {
    const nodeId = selectedHighlightNodeIdMapping.value[subgraphVars.selectedValues[0]]
    const nodePoint = layouts.nodes[nodeId]
  }

})
watch(() => subgraphVars.filterType, () => {
  subgraphVars.selectedValues = []
  subgraphVars.options = []
})
watch(() => subgraphVars.selectedValues, (newValue: string[], oldValue: string[]) => {
  const valuesShouldBeHighlighted = newValue.filter(x => !oldValue.includes(x))
  const valuesShouldBeIgnored = oldValue.filter(x => !newValue.includes(x))

  const nodeIds = Object.keys(nodes)
  valuesShouldBeHighlighted.forEach(value => {
    let nodeId: any
    switch (subgraphVars.filterType) {
      case 'sid': nodeId = value; break;
      case 'alias': nodeId = nodeIds.find(_nodeId => nodes[_nodeId].alias === value); console.log(`alias: ${nodeId}`); break;
      case 'name': nodeId = nodeIds.find(_nodeId => nodes[_nodeId].name === value); console.log(`name: ${nodeId}`); break;
      default: nodeId = undefined;
    }
    if (nodeId) {
      selectedHighlightNodeIdMapping.value[value] = nodeId
      if (nodeIds.includes(nodeId))
        nodes[nodeId].color = Colors.default.WARNING_COLOR + '99'
    }
  })

  valuesShouldBeIgnored.forEach(value => {
    const nodeId = selectedHighlightNodeIdMapping.value[value]
    if (nodeIds.includes(nodeId))
      nodes[nodeId].color = normalNodeStyle.color
  })
})
watch(selectedNodes, () => {
  if (selectedNodes.value.length > 0) {
    const neighborNodes = new Set<string>()
    const connectedEdges = new Set<string>()
    for (const nodeId of selectedNodes.value) {
      for (const edgeId in edges) {
        const edge = edges[edgeId]
        if (edge.source === nodeId || edge.target === nodeId) {
          neighborNodes.add(edge.source === nodeId ? edge.target : edge.source)
          connectedEdges.add(edgeId)
        }
      }
    }
    highlightNodes.value = [...neighborNodes]
    highlightEdges.value = [...connectedEdges]
  }
  else {
    highlightNodes.value = []
    highlightEdges.value = []
  }
})
watch(highlightNodes, () => {
  // highlight nodes
  if (highlightNodes.value.length > 0 && selectedNodes.value.length > 0) {
    const selectedNodeIds = new Set(selectedNodes.value)
    const nodesToHighlight = new Set<string>(highlightNodes.value)
    for (const nodeId in nodes) {
      if (nodesToHighlight.has(nodeId) || selectedNodeIds.has(nodeId)) {
        Object.assign(nodes[nodeId], highlightedNodeStyle)
      } else {
        Object.assign(nodes[nodeId], otherNodeStyle)
      }
    }
  }
  else {
    for (const nodeId in nodes) {
      Object.assign(nodes[nodeId], normalNodeStyle)

      // workaround for color of selected nodes in select
      const hasNodeId = subgraphVars.selectedValues.find((value: string) => selectedHighlightNodeIdMapping.value[value] === nodeId)
      if (hasNodeId)
        nodes[nodeId].color = Colors.default.WARNING_COLOR + '99'
    }
  }
})
watch(highlightEdges, () => {
  if (highlightEdges.value.length > 0 && selectedNodes.value.length > 0) {
    const edgesToHighlight = new Set<string>(highlightEdges.value)
    for (const edgeId in edges) {
      if (edgesToHighlight.has(edgeId)) {
        Object.assign(edges[edgeId], highlightedEdgeStyle)
      } else {
        Object.assign(edges[edgeId], otherEdgeStyle)
      }
    }
  }
  else {
    for (const edgeId in edges) {
      Object.assign(edges[edgeId], normalEdgeStyle)
    }
  }
})

// composition api: computed
const configNodeLabelText = computed({
  get(): string {
    return (configs.node?.label?.text?.toString() ?? "")
  },
  set(value: string) {
    if (configs.node?.label?.text !== undefined)
      configs.node.label.text = value
  }
})
const configNodeLabelVisible = computed({
  get(): boolean {
    return (configs.node?.label?.visible as boolean)
  },
  set(value: boolean) {
    if (configs.node?.label?.visible !== undefined)
      configs.node.label.visible = value
  }
})
const configViewScalingObjects = computed({
  get(): boolean {
    return (configs.view?.scalingObjects as boolean)
  },
  set(value: boolean) {
    if (configs.view?.scalingObjects !== undefined)
      configs.view.scalingObjects = value
  }
})
const configViewLayoutHandler = computed({
  get: (): boolean => {
    return (configs.view?.layoutHandler instanceof ForceLayout)
  },
  set: (value: boolean) => {
    if (configs.view !== undefined) {
      if (value) {
        configs.view.layoutHandler = new ForceLayout({
          positionFixedByDrag: true,
          positionFixedByClickWithAltKey: true,
        })
      }
      else {
        if (configs.view.layoutHandler !== undefined)
          configs.view.layoutHandler = new SimpleLayout()
      }
    }
  }
})
const tooltipPos = computed({
  get: () => {
    if (!graph.value || !tooltip.value) return { x: 0, y: 0 }
    if (tooltipVars.targetId == "") return { x: 0, y: 0 }

    try {
      if (tooltipVars.targetType == "node") {
        const nodePos = layouts.nodes[tooltipVars.targetId]
        const domPoint = graph.value.translateFromSvgToDomCoordinates(nodePos)
        return {
          left: domPoint.x - tooltip.value.offsetWidth / 2 + "px",
          top: domPoint.y - 16 - tooltip.value.offsetHeight - 10 + "px",
        }
      }
      else if (tooltipVars.targetType == "edge") {
        const sourceNode = edges[tooltipVars.targetId].source
        const targetNode = edges[tooltipVars.targetId].target
        const edgePos = {
          x: (layouts.nodes[sourceNode].x + layouts.nodes[targetNode].x) / 2,
          y: (layouts.nodes[sourceNode].y + layouts.nodes[targetNode].y) / 2,
        }
        const domPoint = graph.value.translateFromSvgToDomCoordinates(edgePos)
        return {
          left: domPoint.x - tooltip.value.offsetWidth / 2 + "px",
          top: domPoint.y - 4 - tooltip.value.offsetHeight - 10 + "px",
        }
      }
      else {
        return { x: 0, y: 0 }
      }
    } catch (error) {
      console.log(error)
      return { x: 0, y: 0 }
    }
  },
  set: () => {
    // do nothing
  }
})
const esunLogoURL = computed({
  get: () => {
    return require("@/assets/esun.logo.trans.png")
  },
  set: () => {
    // do nothing
  }
})
</script>


<style lang="scss" scoped>
@use "@/styles/main.scss" as main;
@font-face {
  font-family: "Material Icons";
  font-style: normal;
  font-weight: 400;
  src: url("../assets/fonts/MaterialIcons-Regular.ttf") format("truetype");
}
@font-face {
  font-family: "Noto Sans TC";
  src: local("Noto Sans TC"),
    url("../assets/fonts/NotoSansTC-Regular.otf") format("opentype");
}
.collapse-btn {
  margin-left: auto;
  margin-right: 0;
  height: 18px;
  width: 18px;
  opacity: 0.8;
  left: 14px;
}

.control {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-left: 10px;
  width: 100%;
}

.stretch-to-max {
  width: 100%;
  height: 100%;
}

.control-panel {
  margin: 10px;
  background: main.$bg-3;
  border-radius: 4px;
  padding: 0px 14px 32px 14px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: center;
  border: 1px solid main.$border-2;
  overflow: hidden;
  width: 35vw;
  height: 15vh;
  min-height: 120px;
  min-width: 560px;
  label {
    font-family: "Noto Sans TC", sans-serif;
    font-size: 13px;
    margin: 10px;
  }
  position: fixed;
  font-family: "Noto Sans TC", sans-serif;
  //cursor: move;
}

.control-panel-closed{
  height: 8px;
  min-height: 18px;
  padding: 0 14px 0 14px;
}

.tooltip-wrapper {
  position: relative;
  height: 100%;
  font-family: "Noto Sans TC", sans-serif;
}
.tooltip {
  top: 0;
  left: 0;
  opacity: 0;
  position: absolute;
  min-width: 100px;
  // height: 36px;
  padding: 5px;
  text-align: left;
  font-size: 12px;
  background-color: #fff0bd;
  border: 1px solid #ffb950;
  box-shadow: 2px 2px 2px #aaa;
  border-radius: 8px;
  z-index: 2;
  //transition: opacity 0.2s linear;
  font-family: "Noto Sans TC", sans-serif;
}

.unselectable {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  -o-user-select: none;
  user-select: none;
}
</style>
