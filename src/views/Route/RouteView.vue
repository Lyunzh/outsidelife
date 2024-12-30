<template>
  <div class="route-container">
    <!-- 路线基本信息 -->
    <el-card class="route-info-card">
      <p class="route-description">{{ route.description }}</p>
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
    
    <!-- 添加地图容器 -->
    <el-card class="map-container">
      <template #header>
        <div class="card-header">
          <span>路线地图</span>
        </div>
      </template>
      <div id="container"></div>
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
import AMapLoader from "@amap/amap-jsapi-loader";
import { parseLocation } from '@/utils/coordTransform';

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
      // 添加模拟数据
      mockRoutePool: [
        {
          routeId: 1,
          routeName: "古猗园-佘山徒步线",
          routeDescription: "一条轻松的徒步路线，带您领略古猗园的园林之美",
          time: "2小时",
          nodes: [
            {
              spotId: 1,
              spotName: "古猗园",
              location: "121.212401, 31.282552",
              description: "路线起点，古猗园大门"
            },
            {
              spotId: 2,
              spotName: "佘山",
              location: "121.218022, 31.280645",
              description: "路线终点，佘山景区入口"
            }
          ]
        },
        {
          routeId: 2,
          routeName: "佘山-滨江公园线",
          routeDescription: "经典的佘山登山路线，包含主要景观点",
          time: "3小时",
          nodes: [
            {
              spotId: 2,
              spotName: "佘山",
              location: "121.218022, 31.280645",
              description: "路线起点，佘山景区入口"
            },
            {
              spotId: 3,
              spotName: "滨江森林公园",
              location: "121.21748, 31.285429",
              description: "路线终点，滨江森林公园入口"
            }
          ]
        }
      ],
      mockTeams: [
        {
          teamId: 1,
          teamName: "周末徒步队",
          memberCount: 10,
          teamDescription: "每周末组织徒步活动的团队",
          routeIds: [1, 2]  // 关联的路线ID
        },
        {
          teamId: 2,
          teamName: "骑行爱好者",
          memberCount: 15,
          teamDescription: "专注于自行车骑行的团队",
          routeIds: [2]
        }
      ],
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
      },
      map: null,
      markers: [],
    }
  },
  methods: {
    async fetchRouteData(routeId) {
      try {
        const response = await getRouteDetails(routeId);
        this.route = response.data.data;
      } catch(error) {
        this.$message.error('获取路线数据失败');
        
        // 使用模拟数据
        const foundRoute = this.mockRoutePool.find(route => route.routeId === Number(routeId));
        if (foundRoute) {
          this.route = foundRoute;
        } else {
          this.$message.error('未找到该路线');
          this.$router.push('/');
        }
      }
    },

    async fetchTeams(routeId) {
      try {
        const response = await getTeamsByRouteId(routeId);
        this.teams = response.data.data;
      } catch(error) {
        this.$message.error('获取团队数据失败');
        
        // 使用模拟数据
        this.teams = this.mockTeams.filter(team => team.routeIds.includes(Number(routeId)));
      }
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
    },

    async initMap() {
      window._AMapSecurityConfig = {
        securityJsCode: "85c67bb02b04c0775ec33200f09cee35",
      };

      try {
        const AMap = await AMapLoader.load({
          key: "805421f5522082b95ad7d79e57065023",
          version: "2.0",
          plugins: ["AMap.Scale", "AMap.InfoWindow", "AMap.Driving", "AMap.ElasticMarker"],
        });

        // 初始化地图
        this.map = new AMap.Map("container", {
          mapStyle: "amap://styles/macaron",
          viewMode: "3D",
          resizeEnable: true,
          turboMode: true,
          forceVector: true,
          defaultCursor: "pointer",
          showIndoorMap: false,
          showBuildingBlock: true,
          zoom: 16,
          zooms: [5, 20],
          center: parseLocation(this.route.nodes[0].location) // 以第一个节点为中心
        });

        // 为每个节点创建标记
        this.route.nodes.forEach((node, index) => {
          const position = parseLocation(node.location);
          
          // 根据节点类型选择默认图标
          const defaultIcon = {
            img: index === 0 ? 
              "https://webapi.amap.com/theme/v1.3/markers/n/start.png" :
              index === this.route.nodes.length - 1 ? 
              "https://webapi.amap.com/theme/v1.3/markers/n/end.png" :
              "https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png",
            size: [25, 34],
            anchor: "bottom-center"
          };

          const marker = new AMap.ElasticMarker({
            position: position,
            styles: [{
              icon: defaultIcon,
              label: {
                content: `${index + 1}. ${node.spotName}`,
                position: "BM",
                offset: [0, 5]
              }
            }],
            zoomStyleMapping: {  // 确保在所有缩放级别使用相同样式
              3: 0, 4: 0, 5: 0, 6: 0, 7: 0, 8: 0, 9: 0, 10: 0,
              11: 0, 12: 0, 13: 0, 14: 0, 15: 0, 16: 0, 17: 0,
              18: 0, 19: 0, 20: 0
            }
          });

          // 添加点击事件
          marker.on('click', () => {
            const infoWindow = new AMap.InfoWindow({
              content: `<div style="padding:10px;">
                        <h4>${node.spotName}</h4>
                        <p>${node.description}</p>
                       </div>`,
              offset: new AMap.Pixel(0, -30)
            });
            infoWindow.open(this.map, position);
          });

          this.markers.push(marker);
        });

        // 创建路线导航
        const driving = new AMap.Driving({
          map: this.map,
          hideMarkers: true,  // 隐藏默认标记，使用我们自定义的标记
          autoFitView: true
        });

        // 规划路线
        if (this.route.nodes.length > 1) {
          driving.search(
            parseLocation(this.route.nodes[0].location),  // 起点
            parseLocation(this.route.nodes[this.route.nodes.length - 1].location),  // 终点
            {
              waypoints: this.route.nodes.slice(1, -1).map(node => parseLocation(node.location))  // 途经点
            },
            (status, result) => {
              if (status === 'complete') {
                const infoWindow = new AMap.InfoWindow({
                  content: `<div style="padding:10px;">
                            <h4>${this.route.routeName}</h4>
                            <p>总距离：${(result.routes[0].distance / 1000).toFixed(2)}公里</p>
                            <p>预计时间：${this.route.time}</p>
                           </div>`,
                  offset: new AMap.Pixel(0, -30)
                });
                infoWindow.open(this.map, parseLocation(this.route.nodes[0].location));
              }
            }
          );
        }

        // 添加所有标记
        this.map.add(this.markers);
        this.map.setFitView();

      } catch (error) {
        console.error('地图初始化失败:', error);
      }
    }
  },

  created() {
    const routeId = this.$route.params.id;
    this.routeId = routeId;

    if (!this.routeId) {
      this.$message.error('无效的路线ID');
      this.$router.push('/');
      return;
    }
  },

  async mounted() {
    if (this.routeId) {
      await this.fetchRouteData(this.routeId);
      await this.fetchTeams(this.routeId);
      await this.initMap();  // 添加地图初始化
    }
  },

  beforeUnmount() {
    if (this.map) {
      if (this.markers.length > 0) {
        this.map.remove(this.markers);
      }
      this.map.destroy();
      this.map = null;
    }
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

.map-container {
  margin: 20px 0;
}

#container {
  width: 100%;
  height: 500px;
  border-radius: 4px;
  overflow: hidden;
}
</style>

