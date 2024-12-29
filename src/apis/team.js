import request from '@/utils/request'

export function createTeam(newTeam,routeId){
  return request({
    url: `/team/create/${routeId}`,
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

export const reportTeam = (reportData) => {
  return request({
    url: '/team/report',
    method: 'post',
    data: reportData
  });
};