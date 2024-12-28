import request from '@/utils/request';

export function getRouteDetails(routeId){
    return request({
        url: `/route/${routeId}`,
        method: 'GET'
    })
}

