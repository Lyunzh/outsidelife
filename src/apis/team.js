import request from '@/utils/request'

export function createTeam(newTeam){
  return request({
    url: '/team/create',
    method: 'post',
    data: newTeam   
  })
}

export function joinTeam(teamId){
  return request({
    url: `/team/join/${teamId}`,
    method: 'post'
  })
}

export function getTeamsByRouteId(routeId){
  return request({
    url: `/team/all/${routeId}`,
    method: 'get'
  })
}
