<template>
  <div class="route-container">
    <!-- 路线基本信息 -->
    <el-card class="route-info-card">
      <h2>{{ route.routeName }}</h2>
      <p class="route-description">{{ route.routeDescription }}</p>
      <div class="route-meta">
        <span><i class="el-icon-time"></i> 预计时长：{{ route.time }}</span>
      </div>
      
      <!-- 路线节点信息 -->
      <div class="route-nodes">
        <h3>路线节点</h3>
        <el-timeline>
          <el-timeline-item
            v-for="node in route.nodes"
            :key="node.nodeId"
            :timestamp="node.time">
            {{ node.description }}
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>

    <!-- 团队列表 -->
    <el-card class="teams-card">
      <template #header>
        <div class="card-header">
          <span>活动团队</span>
          <el-button type="primary" @click="showCreateTeamDialog">
            创建新团队
          </el-button>
        </div>
      </template>

      <div class="teams-list">
        <el-card v-for="team in teams" :key="team.teamId" class="team-item">
          <div class="team-info">
            <h3>{{ team.teamName }}</h3>
            <p>{{ team.description }}</p>
            <div class="team-meta">
              <span>人数：{{ team.currentMembers }}/{{ team.limitCnt }}</span>
              <span>出发时间：{{ team.endTime }}</span>
            </div>
          </div>
          <div class="team-actions">
            <el-button 
              type="primary" 
              :disabled="team.currentMembers >= team.maxMembers || team.isJoined"
              @click="joinTeam(team.id)">
              {{ team.currentMembers >= team.maxMembers ? '人数已满' : (team.isJoined ? '已加入' : '加入团队') }}
            </el-button>
            <el-button 
              type="danger" 
              plain
              @click="showReportDialog(team)">
              举报
            </el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 创建团队对话框 -->
    <el-dialog
      title="创建新团队"
      v-model="createTeamDialogVisible"
      width="500px">
      <el-form :model="newTeam" label-width="100px">
        <el-form-item label="团队名称">
          <el-input v-model="newTeam.teamName"></el-input>
        </el-form-item>
        <el-form-item label="团队简介">
          <el-input type="textarea" v-model="newTeam.description"></el-input>
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="newTeam.limitCnt" :min="2" :max="20"></el-input-number>
        </el-form-item>
        <el-form-item label="出发时间">
          <el-date-picker
            v-model="newTeam.endTime"
            type="datetime"
            placeholder="选择出发时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createTeamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createTeam">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加举报对话框 -->
    <el-dialog
      title="举报团队"
      v-model="reportDialogVisible"
      width="500px">
      <el-form :model="reportData" label-width="100px">
        <el-form-item label="举报原因">
          <el-select  placeholder="请选择举报原因">
            <el-option label="虚假信息" value="fake_info" />
            <el-option label="不当内容" value="inappropriate_content" />
            <el-option label="违规行为" value="violation" />
            <el-option label="其他原因" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input
            type="textarea"
            v-model="reportData.description"
            rows="4"
            placeholder="请详细描述举报原因">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReport">提交举报</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getRouteDetails } from '@/apis/route';
import { createTeam, getTeamsByRouteId, joinTeam, reportTeam } from '@/apis/team';

export default {
  name: 'RouteView',
  data() {
    return {
      route: {
        routeName: '',
        routeDescription: '',
        time: '',
        nodes: []
      },
      routeId: null,
      teams: [],
      createTeamDialogVisible: false,
      newTeam: {
        teamName: '',
        description: '',
        limitCnt: 2,
        endTime: null
      },
      reportDialogVisible: false,
      reportData: {
        teamId: null,
        description: ''
      }
    }
  },
  methods: {
    async fetchRouteData(routeId) {
      const response = await getRouteDetails(routeId);
      this.route = response.data.data;
    },

    async fetchTeams(routeId) {
      const response = await getTeamsByRouteId(routeId);
      this.teams = response.data.data;
    },

    showCreateTeamDialog() {
      this.createTeamDialogVisible = true;
    },

    async joinTeam(teamId) {
      const response = await joinTeam(teamId);
      if(response.status===200){
        this.$message.success('加入团队成功');
      }else{
        this.$message.error('加入团队失败');
      }
      this.fetchTeams(this.routeId);
    },

    async createTeam() {
      const response = await createTeam(this.newTeam,this.routeId);
      if(response.status===200){
        this.$message.success('创建团队成功');
      }else{
        this.$message.error('创建团队失败');
      }
      this.fetchTeams(this.routeId);
      this.createTeamDialogVisible = false;
    },

    showReportDialog(team) {
      this.reportData.teamId = team.teamId;
      this.reportDialogVisible = true;
    },

    async submitReport() {
      try {
        const response = await reportTeam(this.reportData);
        if (response.status === 200) {
          this.$message.success('举报已提交');
          this.reportDialogVisible = false;
          // 重置表单
          this.reportData = {
            teamId: null,
            reason: '',
            description: ''
          };
        } else {
          this.$message.error('举报提交失败');
        }
      } catch (error) {
        console.error('举报失败:', error);
        this.$message.error('举报提交失败');
      }
    }
  },
  mounted() {
    this.fetchRouteData(this.routeId);
    this.fetchTeams(this.routeId);
  }
}
</script>

<style scoped>
.route-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.route-details {
  margin-bottom: 20px;
}

.route-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.route-info-card {
  margin-bottom: 20px;
}

.route-description {
  color: #666;
  margin: 15px 0;
  line-height: 1.6;
}

.route-meta {
  color: #666;
  margin: 10px 0;
}

.route-nodes {
  margin-top: 20px;
}

.teams-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.teams-list {
  display: grid;
  gap: 20px;
}

.team-item {
  border: 1px solid #eee;
}

.team-info {
  margin-bottom: 15px;
}

.team-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 0.9em;
  margin-top: 10px;
}

.team-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer {
  text-align: right;
}

.route-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

.route-timeline {
  margin-top: 20px;
}

.el-timeline-item {
  padding-bottom: 20px;
}

.team-item {
  transition: all 0.3s ease;
}

.team-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.team-info h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.team-info p {
  color: #666;
  line-height: 1.5;
}

.el-button {
  padding: 8px 20px;
}

.el-dialog__body {
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

/* 响应式布局优化 */
@media (max-width: 768px) {
  .route-container {
    padding: 10px;
  }
  
  .team-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .el-dialog {
    width: 90% !important;
  }
}

.team-actions .el-button + .el-button {
  margin-left: 10px;
}

.el-select {
  width: 100%;
}
</style>

