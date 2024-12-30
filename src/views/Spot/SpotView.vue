<template>
  <div class="spot-container">
    <!-- 景点图片展示区 -->
    <div class="spot-image-section">
      <el-carousel height="400px">
        <img :src="spot.imageUrl" class="spot-image" />
      </el-carousel>
    </div>

    <!-- 景点基本信息区 -->
    <div class="spot-info-section">
      <h1 class="spot-title">{{ spot.spotName }}</h1>
      <div class="spot-location">
        <i class="el-icon-location"></i>
        <span>{{ spot.location }}</span>
      </div>
    </div>

    <!-- 景点详细介绍区 -->
    <el-card class="spot-description-section">
      <template #header>
        <div class="card-header">
          <span>景点介绍</span>
        </div>
      </template>
      <p>景点位置：{{ spot.location }}</p>
      <p>景点介绍：{{ spot.description }}</p>
    </el-card>

    <!-- 推荐路线区 -->
    <el-card class="spot-routes-section">
      <template #header>
        <div class="card-header">
          <span>推荐路线</span>
        </div>
      </template>
      <el-collapse>
        <el-collapse-item 
          v-for="route in routes" 
          :key="route.routeId"
          :title="route.routeName"
        >
          <div @click="getRouteDetail(route.routeId)" class="route-info">
            <div class="route-basic-info">
              <span class="route-duration">
                <i class="el-icon-time"></i>
                预计时长：{{ route.time }}
              </span>
              <span class="route-distance">
                <i class="el-icon-map-location"></i>
                <img :src="route.imageUrl" class="route-map" />
              </span>
              <span class="route-difficulty">
                <i class="el-icon-warning"></i>
                难度：{{ route.difficulty }}
              </span>
            </div>
            <div class="route-description">
              {{ route.description }}
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <!-- 添加地图容器 -->
    <el-card class="map-container">
      <template #header>
        <div class="card-header">
          <span>景点地图</span>
        </div>
      </template>
      <div id="container"></div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue';
import AMapLoader from "@amap/amap-jsapi-loader";
import { getSpotDetails , getSpotRoutes} from '@/apis/spot';
import { basePicturesPath } from '@/utils/alldata';
import { getUserIdentity } from '@/apis/identity';

export default {
  name: 'SpotView',
  data() {
    return {
      spotId: null,
      spot: {
        imageUrl: '',
        spotName: '',
        location: '',
        description: ''
      },
      routes: ref([]),
      routeId: null,
      userIdentity: null,
      // 景点数据池
      spotPool: [
        {
          spotId: 1,
          imageUrl: '/images/spots/guyiyuan.jpg',
          spotName: '古猗园',
          location: [121.212401, 31.282552],
          description: '古猗园是上海市著名的古典园林，位于松江区，建于明代。园内亭台楼阁错落有致，花木扶疏，是一处极具江南特色的园林。',
          category: '徒步',
          routes: [1, 2, 3]  // 关联的路线ID
        },
        {
          spotId: 2,
          imageUrl: '/images/spots/sheshan.jpg',
          spotName: '佘山',
          location: [121.218022, 31.280645],
          description: '佘山是上海市最高峰，海拔100米，是著名的登山胜地。山上有著名的佘山天主教堂，是上海市重要的宗教文化景观。',
          category: '徒步',
          routes: [1, 4, 2]
        },
        {
          spotId: 3,
          imageUrl: '/images/spots/binjiang.jpg',
          spotName: '滨江森林公园',
          location: [121.21748, 31.285429],
          description: '滨江森林公园是上海市最大的生态型森林公园，占地面积1000公顷，是市民休闲娱乐的好去处。',
          category: '骑行',
          routes: [2, 3, 1]
        }
      ],
      // 路线数据池 - 只存储景点序列
      routePool: {
        1: [1, 2],    // 古猗园 -> 佘山
        2: [2, 3],    // 佘山 -> 滨江
        3: [3, 1],    // 滨江 -> 古猗园
        4: [2]        // 佘山环线
      },
      mockUserIdentity: {
        role: 'user',
        userId: 1,
        username: 'test_user'
      },
      map: null,
      markers: []
    }
  },
  methods: {
    async fetchSpotData(spotId) {
      try {
        const response = await getSpotDetails(spotId);
        this.spot = response.data.data;
        this.spot.imageUrl = basePicturesPath + response.data.data.imageUrl;
      } catch(error) {
        this.$message.error('获取景点数据失败');
        // 使用模拟数据
        const foundSpot = this.spotPool.find(spot => spot.spotId === Number(spotId));
        if (foundSpot) {
          this.spot = foundSpot;
        } else {
          this.$message.error('未找到该景点');
          this.$router.push('/');
        }
      }
    },

    async fetchSpotRoutes(spotId) {
      try {
        const response = await getSpotRoutes(spotId);
        this.routes = response.data.data;
      } catch(error) {
        this.$message.error('获取路线数据失败');
        // 使用模拟数据
        const foundSpot = this.spotPool.find(spot => spot.spotId === Number(spotId));
        if (foundSpot) {
          this.routes = foundSpot.routes.map(routeId => {
            const spots = this.routePool[routeId];
            if (!spots) return null;

            const startSpot = this.spotPool.find(s => s.spotId === spots[0]);
            const endSpot = this.spotPool.find(s => s.spotId === spots[spots.length - 1]);

            return {
              id: routeId,
              name: spots.length === 1
                ? `${startSpot.spotName}环线`
                : `${startSpot.spotName}-${endSpot.spotName}线`,
              time: spots.length === 1 ? "1.5小时" : "2小时",
              description: `从${startSpot.spotName}到${endSpot.spotName}的精品路线`,
              imageUrl: "/images/routes/default.jpg",
              difficulty: spots.length === 1 ? "简单" : "中等",
              spots: spots
            };
          }).filter(route => route);
        } else {
          this.routes = [];
        }
      }
    },

    getRouteDetail(routeId) {
      this.$router.push(`/route/${routeId}`);
    },

    async fetchUserIdentity() {
      try {
        const response = await getUserIdentity();
        this.userIdentity = response.data.data;
      } catch(error) {
        this.$message.error('获取用户身份失败');
        // 使用模拟数据
        this.userIdentity = this.mockUserIdentity;
      }
    },

    async initMap() {
      try {
        // 确保在组件挂载后再初始化地图
        if (!document.getElementById('container')) {
          console.error('地图容器不存在');
          return;
        }

        // 设置安全密钥
        window._AMapSecurityConfig = {
          securityJsCode: "85c67bb02b04c0775ec33200f09cee35",
        };

        const AMap = await AMapLoader.load({
          key: "805421f5522082b95ad7d79e57065023",
          version: "2.0",
          plugins: ["AMap.Scale", "AMap.InfoWindow", "AMap.Driving", "AMap.ElasticMarker"],
        });

        // 确保地图实例存在时才销毁
        if (this.map) {
          this.map.destroy();
        }

        // 初始化地图
        this.map = new AMap.Map("container", {
          mapStyle: "amap://styles/macaron",
          viewMode: "3D",
          zoom: 16,
          center: this.spot.location || [121.212401, 31.282552], // 添加默认中心点
        });

        // 添加标记前清除旧标记
        if (this.markers.length > 0) {
          this.map.remove(this.markers);
          this.markers = [];
        }

        // 创建当前景点的特殊标记
        const currentMarker = new AMap.ElasticMarker({
          position: this.spot.location,
          styles: [{
            icon: {
              img: "https://a.amap.com/jsapi_demos/static/resource/img/tingzi.png",
              size: [48, 48],  // 更大的尺寸
              anchor: "bottom-center",
              fitZoom: 14,
              scaleFactor: 2.5,  // 更大的缩放系数
            },
            label: {
              content: this.spot.spotName,
              position: "BM",
              minZoom: 15,
            },
          }],
          zoomStyleMapping: { 14: 0, 15: 0, 16: 0, 17: 0, 18: 0, 19: 0, 20: 0 },
        });

        this.markers.push(currentMarker);

        // 为每个路线创建导航和标记
        this.routes.forEach(route => {
          const spots = route.spots;
          if (spots.length > 1) {
            // 为路线上的每个景点（除了当前景点）创建标记
            spots.forEach(spotId => {
              if (spotId !== Number(this.spotId)) {  // 跳过当前景点
                const spot = this.spotPool.find(s => s.spotId === spotId);
                if (spot) {
                  const marker = new AMap.ElasticMarker({
                    position: spot.location,
                    styles: [{
                      icon: {
                        img: "https://a.amap.com/jsapi_demos/static/resource/img/men3.png",
                        size: [36, 36],  // 普通尺寸
                        anchor: "bottom-center",
                        fitZoom: 14,
                        scaleFactor: 2,  // 普通缩放系数
                      },
                      label: {
                        content: spot.spotName,
                        position: "BM",
                        minZoom: 15,
                      },
                    }],
                    zoomStyleMapping: { 14: 0, 15: 0, 16: 0, 17: 0, 18: 0, 19: 0, 20: 0 },
                  });

                  // 添加点击事件
                  marker.on('click', () => {
                    this.$router.push(`/spot/${spot.spotId}`);
                  });

                  this.markers.push(marker);
                }
              }
            });

            // 创建路线导航
            const driving = new AMap.Driving({
              map: this.map,
              hideMarkers: true,  // 隐藏默认标记，使用我们自定义的标记
              autoFitView: true
            });

            // 获取起点和终点的位置
            const startSpot = this.spotPool.find(s => s.spotId === spots[0]);
            const endSpot = this.spotPool.find(s => s.spotId === spots[spots.length - 1]);

            // 规划路线
            driving.search(
              startSpot.location,
              endSpot.location,
              {
                waypoints: spots.slice(1, -1).map(spotId =>
                  this.spotPool.find(s => s.spotId === spotId).location
                )
              },
              (status, result) => {
                if (status === 'complete') {
                  const infoWindow = new AMap.InfoWindow({
                    content: `<div style="padding:10px;">
                              <h4>${route.name}</h4>
                              <p>总距离：${(result.routes[0].distance / 1000).toFixed(2)}公里</p>
                              <p>预计时间：${route.time}</p>
                              <p>难度：${route.difficulty}</p>
                             </div>`,
                    offset: new AMap.Pixel(0, -30)
                  });
                  infoWindow.open(this.map, startSpot.location);
                }
              }
            );
          }
        });

        // 最后一次性添加所有标记
        this.map.add(this.markers);
        this.map.setFitView();

      } catch (error) {
        console.error('地图初始化失败:', error);
      }
    }
  },

  created() {
    // 使用 $route.params 获取路由参数
    const spotId = this.$route.params.id;
    this.spotId = spotId;

    if (!this.spotId) {
      this.$message.error('无效的ID');
      this.$router.push('/');
      return;
    }
  },

  async mounted() {
    if (this.spotId) {
      try {
        await this.fetchSpotData(this.spotId);
        await this.fetchSpotRoutes(this.spotId);
        await this.initMap();
      } catch (error) {
        console.error('初始化失败:', error);
      }
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
.spot-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.spot-image-section {
  margin-bottom: 30px;
}

.spot-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-info-section {
  margin-bottom: 30px;
  text-align: center;
}

.spot-title {
  font-size: 2em;
  margin-bottom: 10px;
}

.spot-location {
  color: #666;
  font-size: 1.1em;
}

.spot-description-section,
.spot-routes-section {
  margin-bottom: 30px;
}

.card-header {
  font-size: 1.2em;
  font-weight: bold;
}

.route-info {
  padding: 10px;
}

.route-basic-info {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
}

.route-description {
  margin-bottom: 15px;
  line-height: 1.6;
}

.route-steps ol {
  padding-left: 20px;
  line-height: 1.8;
}

.map-container {
  margin-bottom: 30px;
}

#container {
  width: 100%;
  height: 500px;
  border-radius: 4px;
  overflow: hidden;
}
</style>