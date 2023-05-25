<template>
  <PageContainer />
</template>

<script lang="ts">
import { defineComponent } from "vue"
import PageContainer from "./components/PageContainer.vue"

export default defineComponent({
  name: "App",
  components: {
    PageContainer
  },
});
</script>

<style lang="scss">
@use '@/styles/main.scss' as main;
:root {
  --el-color-primary: #00a19b; //override the colors
}
</style>

