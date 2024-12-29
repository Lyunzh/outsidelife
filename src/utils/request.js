import axios from 'axios'

const service = axios.create({
    baseURL: 'http://localhost:8085',
    timeout: 5000
})

// 响应拦截器
service.interceptors.response.use(
    response => {
        console.log('收到响应:', response)
        return response
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

export default service