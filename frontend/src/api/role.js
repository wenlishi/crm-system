import request from '@/utils/request'

// 分页查询角色
export function getRoleList(params) {
  return request({
    url: '/roles/page',
    method: 'get',
    params
  })
}

// 查询角色详情
export function getRoleDetail(id) {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

// 新增角色
export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/roles',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

// 获取所有角色
export function getAllRoles() {
  return request({
    url: '/roles',
    method: 'get',
    params: { all: true }
  })
}

// 获取角色权限
export function getRolePermissions(roleId) {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: 'get'
  })
}

// 分配角色权限
export function assignRolePermissions(roleId, permissionIds) {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: 'put',
    data: { permissionIds }
  })
}
