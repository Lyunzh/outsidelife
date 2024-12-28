import request from '../utils/request'

export function registeruser(userregister={}){
    return request({
        method: 'POST',
        url: '/user/register',
        data:userregister
    })
}

export function registeradmin(adminregister={}){
    return request({
        method: 'POST',
        url: '/admin/register',
        data:adminregister
    })
}


export function sendCode(email){
    return request({
        method: 'POST',
        url: `/email/${email}`
    })
}
