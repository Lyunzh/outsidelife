import request from '@/utils/request';

export function getSpotDetails(spotId){
    return request({
        url: `/spot/${spotId}`,
        method: 'GET'
    })
}

export function getSpotRoutes(spotId){
    return request({
        url: `/route/all/${spotId}`,
        method: 'GET'
    })
}
