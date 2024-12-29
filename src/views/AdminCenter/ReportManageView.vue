<template>
  <div class="report-manage-container">
    <el-card class="report-list">
      <template #header>
        <div class="card-header">
          <span>举报管理</span>
          <el-radio-group v-model="filterStatus" @change="filterReports">
            <el-radio-button label="all">全部</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="reports" style="width: 100%">
        <el-table-column prop="teamName" label="团队名称" width="180" />
        <el-table-column prop="description" label="详细说明" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'pending' ? 'warning' : 'success'">
              {{ scope.row.status === 'pending' ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'pending'"
              type="success"
              size="small"
              @click="active(scope.row.teamId)"
            >
              解除举报
            </el-button>
            <el-button
              v-if="scope.row.status === 'pending'"
              type="danger"
              size="small"
              @click="ban(scope.row.teamId)"
            >
              封禁团队
            </el-button>
            <el-button
              v-if="scope.row.status !== 'pending'"
              type="info"
              size="small"
              disabled
            >
              已处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getReports, activeReport, banReport } from '@/apis/admin'

export default {
  name: 'ReportManageView',
  setup() {
    const reports = ref([])
    const filterStatus = ref('all')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const reasonMap = {
      'fake_info': '虚假信息',
      'inappropriate_content': '不当内容',
      'violation': '违规行为',
      'other': '其他原因'
    }

    const fetchReports = async () => {
      try {
        const response = await getReports({
          page: currentPage.value,
          pageSize: pageSize.value,
          status: filterStatus.value
        })
        reports.value = response.data.data.list
        total.value = response.data.data.total
      } catch (error) {
        ElMessage.error('获取举报列表失败')
      }
    }

    const handlePageChange = (page) => {
      currentPage.value = page
      fetchReports()
    }

    const filterReports = () => {
      currentPage.value = 1
      fetchReports()
    }

    const active = async (teamId) => {
      try {
        await activeReport(teamId)
        ElMessage.success('处理成功')
        fetchReports()
      } catch (error) {
        ElMessage.error('处理失败')
      }
    }

    const ban = async (teamId) => {
      try {
        await banReport(teamId)
        ElMessage.success('处理成功')
        fetchReports()
      } catch (error) {
        ElMessage.error('处理失败')
      }
    }

    const formatDate = (date) => {
      return new Date(date).toLocaleString()
    }

    const getReasonText = (reason) => {
      return reasonMap[reason] || reason
    }

    // 初始加载数据
    fetchReports()

    return {
      reports,
      filterStatus,
      currentPage,
      pageSize,
      total,
      handlePageChange,
      filterReports,
      active,
      ban,
      formatDate,
      getReasonText
    }
  }
}
</script>

<style scoped>
.report-manage-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.el-table {
  margin-top: 20px;
}
</style> 