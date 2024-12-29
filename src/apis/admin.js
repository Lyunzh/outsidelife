
import request from '@/utils/request'

// 获取举报列表
export const getReports = () => {
  return request({
    url: '/report/list',
    method: 'get',
  })
}

// 处理举报
export const activeReport = (teamId) => {
  return request({
    url: `/report/active/${teamId}`,
    method: 'post',
  })
}


export const banReport = (teamId) => {
  return request({
    url: `/report/ban/${teamId}`,
    method: 'post',
  })
}

