<template>
  <el-container class="page-container">
    <el-container>
      <el-main style="overflow: hidden">
        <DiagramContainer />
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts" setup>
import DiagramContainer from "@/components/DiagramContainer.vue"
import { disablePageScrolling } from "@/utils/screen"
import { onMounted } from "vue";

onMounted(() => {
  disablePageScrolling()
})

</script>

<style lang="scss" scoped>
@use '@/styles/main.scss' as main;
.page-container {
  height: 98vh;
  border: 1px solid main.$border-2;

  .el-main {
    --el-main-padding: 0
  }
}
</style>
