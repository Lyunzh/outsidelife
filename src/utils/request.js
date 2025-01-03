import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8085',
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        // 从localStorage获取token
        const token = localStorage.getItem('token')
        if (token) {
            // 添加到请求头
            config.headers['Authorization'] = 'Bearer ' + token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

export default request