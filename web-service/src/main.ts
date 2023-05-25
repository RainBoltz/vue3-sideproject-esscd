import { createApp } from "vue";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import VNetworkGraph from "v-network-graph";
import "v-network-graph/lib/style.css";
import App from "./App.vue";

const app = createApp(App);

app.use(ElementPlus);
app.use(VNetworkGraph);
app.mount("#app");
