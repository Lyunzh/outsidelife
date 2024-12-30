const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    client: {
        overlay: false,
      },
    proxy: {
        '/api': {
            target: 'http://localhost:8085',
            ws: true,
            changeOrigin: true,
            pathRewrite: {
                '^/api': ''
            }
        }
    }
}
})