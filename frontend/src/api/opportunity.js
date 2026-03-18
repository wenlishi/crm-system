import request from '@/utils/request'

// 分页查询商机
export function getOpportunityList(params) {
  return request({
    url: '/opportunities/page',
    method: 'get',
    params
  })
}

// 查询商机详情
export function getOpportunityDetail(id) {
  return request({
    url: `/opportunities/${id}`,
    method: 'get'
  })
}

// 新增商机
export function createOpportunity(data) {
  return request({
    url: '/opportunities',
    method: 'post',
    data
  })
}

// 修改商机
export function updateOpportunity(data) {
  return request({
    url: '/opportunities',
    method: 'put',
    data
  })
}

// 删除商机
export function deleteOpportunity(id) {
  return request({
    url: `/opportunities/${id}`,
    method: 'delete'
  })
}

// 获取阶段统计（销售漏斗）
export function getOpportunityStageStats() {
  return request({
    url: '/opportunities/stats/stage',
    method: 'get'
  })
}
