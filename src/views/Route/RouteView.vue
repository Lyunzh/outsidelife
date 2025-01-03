<template>
  <div class="route-container">
    <!-- 修改返回按钮 -->
    <div class="back-button" @click="goBack">
      <el-button type="primary">
        <i class="el-icon-arrow-left"></i>
        <span>返回景点详情</span>
      </el-button>
    </div>

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
            <el-tooltip
              effect="dark"
              placement="top"
              trigger="hover"
            >
              <template #content>
                <div class="node-preview">
                  <img :src="node.imageUrl" class="node-image" />
                  <p>{{ node.description }}</p>
                </div>
              </template>
              <span class="node-name">{{ node.name }}</span>
            </el-tooltip>
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
              <span>人数：{{ team.currentNum }}/{{ team.limitCnt }}</span>
              <span>出发时间：{{ team.endTime }}</span>
            </div>
          </div>
          <div class="team-actions">
            <el-button 
              type="primary" 
              :disabled="team.currentMembers >= team.maxMembers || team.isJoined"
              @click="joinTeam(team.teamId)">
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

    <!-- 添加 AI 聊天组件 -->
    <AIChat />
  </div>
</template>

<script>
import { basePicturesPath } from '@/utils/alldata';
import { getRouteDetails } from '@/apis/route';
import { createTeam, getTeamsByRouteId, joinTeam, reportTeam } from '@/apis/team';
import AMapLoader from "@amap/amap-jsapi-loader";
import { parseLocation } from '@/utils/coordTransform';
import AIChat from '@/components/AIChat.vue';

export default {
  name: 'RouteView',
  components: {
    AIChat
  },
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
      walkingInstances: []
    }
  },
  methods: {
    async fetchRouteData(routeId) {
      try {
        const response = await getRouteDetails(routeId);
        this.route = response.data.data;

        // 为每个节点添加完整的图片路径
        this.route.nodes.forEach(node => {
          node.imageUrl = basePicturesPath + node.imageUrl;
        });
      } catch (error) {
        this.$message.error('获取路线数据失败');
        
        // 使用模拟数据
        const foundRoute = this.mockRoutePool.find(route => route.routeId === Number(routeId));
        if (foundRoute) {
          this.route = foundRoute;
          // 同样处理模拟数据的图片路径
          this.route.nodes.forEach(node => {
            node.imageUrl = basePicturesPath + node.imageUrl;
          });
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

    async createMultiSegmentRoute(points, AMap) {
      return new Promise((resolve) => {
        this.map.plugin(["AMap.Walking"], () => {
          let totalDistance = 0;
          let totalTime = 0;
          const colors = ["#3366FF", "#FF3366", "#33FF66", "#6633FF", "#FF6633"];
          const maxRetries = 3;

          // 创建所有导航实例
          for (let i = 0; i < points.length - 1; i++) {
            const walkingInstance = new AMap.Walking({
              map: this.map,
              hideMarkers: true,
              strokeColor: colors[i % colors.length],
              strokeWeight: 6
            });
            this.walkingInstances.push(walkingInstance);
          }

          // 递归处理每段路线
          const processSegment = (segmentIndex, retryCount = 0) => {
            if (segmentIndex >= points.length - 1) {
              resolve({
                totalDistance,
                totalTime,
                segments: points.length - 1
              });
              return;
            }

            this.walkingInstances[segmentIndex].search(
              points[segmentIndex],
              points[segmentIndex + 1],
              (status, result) => {
                if (status === 'complete') {
                  totalDistance += result.routes[0].distance;
                  totalTime += result.routes[0].time;
                  processSegment(segmentIndex + 1);
                } else {
                  if (retryCount < maxRetries) {
                    setTimeout(() => {
                      console.log(`第${segmentIndex + 1}段路线规划失败，正在进行第${retryCount + 1}次重试...`);
                      processSegment(segmentIndex, retryCount + 1);
                    }, 1000);
                  } else {
                    this.$message.error(`第${segmentIndex + 1}段路线规划失败，已重试${maxRetries}次`);
                    resolve(null);
                  }
                }
              }
            );
          };

          processSegment(0);
        });
      });
    },

    async initMap() {
      window._AMapSecurityConfig = {
        securityJsCode: "85c67bb02b04c0775ec33200f09cee35",
      };

      try {
        const AMap = await AMapLoader.load({
          key: "805421f5522082b95ad7d79e57065023",
          version: "2.0",
          plugins: ["AMap.Scale", "AMap.InfoWindow", "AMap.ElasticMarker"],
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
          pitch: 55,
          rotation: -45,
          zoom: 16,
          zooms: [5, 20],
          center: parseLocation(this.route.nodes[0].location) // 以第一个节点为中心
        });

        // 处理路线节点
        const routePoints = this.route.nodes.map((node, index) => {
          const position = parseLocation(node.location);
          
          // 创建标记点
          const marker = new AMap.ElasticMarker({
            position: position,
            styles: [{
              icon: {
                img: index === 0 ? 
                  "https://webapi.amap.com/theme/v1.3/markers/n/start.png" :
                  index === this.route.nodes.length - 1 ? 
                  "https://webapi.amap.com/theme/v1.3/markers/n/end.png" :
                  "https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png",
                size: [36, 36],
                anchor: "bottom-center",
                imageOffset: [0, 0],
                fitZoom: 14,
                clickable: true,
                scaleFactor: 2,
                maxScale: 2,
                minScale: 1,
              },
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
          return position;
        });

        this.map.add(this.markers);
        this.map.setFitView();

        // 创建导航路线
        const routeInfo = await this.createMultiSegmentRoute(routePoints, AMap);
        
        if (routeInfo) {
          const infoWindow = new AMap.InfoWindow({
            content: `<div style="padding:10px;">
                      <h4>${this.route.routeName}</h4>
                      <p>总距离：${(routeInfo.totalDistance / 1000).toFixed(2)}公里</p>
                      <p>预计时间：${Math.ceil(routeInfo.totalTime / 60)}分钟</p>
                     </div>`,
            offset: new AMap.Pixel(0, -30)
          });
          infoWindow.open(this.map, routePoints[0]);
        }

      } catch (error) {
        console.error('地图初始化失败:', error);
        this.$message.error('地图加载失败，请稍后重试');
      }
    },

    goBack() {
      this.$router.go(-1);
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
      await this.initMap();
    }
  },

  beforeUnmount() {
    if (this.map) {
      if (this.markers.length > 0) {
        this.map.remove(this.markers);
      }
      if (this.walkingInstances.length > 0) {
        this.walkingInstances.forEach(instance => instance.clear());
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

.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
}

.back-button .el-button {
  padding: 12px 20px;
  font-size: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.back-button .el-button i {
  margin: 0;
}

.back-button .el-button span {
  display: inline-block;
  text-align: center;
}

.back-button .el-button:hover {
  transform: translateX(-5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.node-name {
  cursor: pointer;
  text-decoration: underline;
  color: #409EFF;
}

.node-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 150px;
}

.node-image {
  width: 100px;  /* 固定宽度 */
  height: 100px; /* 固定高度 */
  object-fit: cover;  /* 保持比例裁剪 */
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  margin-bottom: 8px;
}

.node-preview p {
  margin: 0;
  font-size: 12px;
  color: #666;
  text-align: center;
}
</style>

