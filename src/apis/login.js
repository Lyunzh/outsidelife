import request from '@/utils/request'

export const loginuser = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export function loginadmin(adminlogin={}){
    return request({
        method: 'POST',
        url: '/admin/login',
        data:adminlogin
    })
}
