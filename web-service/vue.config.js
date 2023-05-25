module.exports = {
  devServer: {
    host: "127.0.0.1",
    port: 8082,
    open: true,
    proxy: {
      "^/api": {
        target: "http://localhost:8080",
        pathRewrite: { "^/api": "" },
        changeOrigin: true,
        secure: false,
        ws: true,
        logLevel: "debug",
      },
    },
  },
  chainWebpack: (config) => {
    if (process.env.NODE_ENV === "development") {
      config.output.filename("[name].[hash].js").end();
    }
  },
};
