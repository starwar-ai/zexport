import request from '@/config/axios'

// Query paginated data
export const getCategoryPage = async (params) => {
  return await request.get({ url: `/crm/category/page`, params })
}

// Create new category
export const createCategory = async (data) => {
  return await request.post({ url: `/crm/category/create`, data })
}

// Get category info by ID
export const getCategoryInfo = async (id) => {
  return await request.get({ url: `/crm/category/get?id=${id}` })
}

// Get category tree
export const getCategoryTree = async () => {
  return await request.get({ url: `/crm/category/get-simple-list` })
}

// Update category
export const updateCategory = async (data) => {
  return await request.put({ url: `/crm/category/update`, data })
}

// Delete category by ID
export const deleteCategory = async (id: number) => {
  return await request.delete({ url: `/crm/category/delete?id=` + id })
}

// Export category data to Excel
export const exportCategory = async (params) => {
  return await request.download({ url: `/crm/category/export-excel`, params })
}
