<template>
  <PageContainer />
</template>

<script setup>
const { data: content } = await useFetch('/api/hello')
</script>
