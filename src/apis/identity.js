import request from "@/utils/request"

export const getUserIdentity = () => {
  return request({
    url: '/identity',
    method: 'GET',
  })
}