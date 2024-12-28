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
      <p> 景点位置：{{ spot.location }}</p>
      <p> 景点介绍：{{ spot.description }}</p>
    </el-card>

    <!-- 推荐路线区 -->
    <el-card class="spot-routes-section">
      <template #header>
        <div class="card-header">
          <span>推荐路线</span>
        </div>
      </template>
      <el-collapse>
        <el-collapse-item v-for="route in spot.routes" :key="route.id" :title="route.name">
          <div @click="getRouteDetail(route.routeId)" class="route-info">
            <div class="route-basic-info">
              <span class="route-duration">
                <i class="el-icon-time"></i>
                预计时长：{{ route.time }}
              </span>
              <span class="route-distance">
                <i class="el-icon-map-location"></i>
              </span>
              <span class="route-difficulty">
                <i class="el-icon-warning"></i>
                难度：{{ route.difficulty }}
              </span>
            </div>
            <div class="route-description">
              {{ route.description }}
            </div>
            <div class="route-steps">
              <ol>
                <li v-for="step in route.steps" :key="step">{{ step }}</li>
              </ol>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue';
import { getSpotDetails , getSpotRoutes} from '@/api/spotApi';
import { basePicturePath } from '@/config/apiConfig';
  export default {
  name: 'SpotView',
  data() {
    return {
      spot: {
        imageUrl: '',
        spotName: '',
        location: '',
        description: ''
      },
      routes: ref([]),
      routeId: null

    }
  },
  methods: {
    async fetchSpotData(spotId){
      try{
        const response = await getSpotDetails(spotId);
        this.spot = response.data.data;
        this.spot.imageUrl = basePicturePath + response.data.data.imageUrl;
      }catch(error){
        this.$message.error('获取景点数据失败');
      }
    },

    async fetchSpotRoutes(spotId){
      try{
        const response = await getSpotRoutes(spotId);
        this.routes = response.data.data;
      }catch(error){
        this.$message.error('获取路线数据失败');
      }
    },

    getRouteDetail(routeId){
      this.$router.push(`/route/${routeId}`);
    }

  },

  created() {
    const pathParts = this.$route.path.split('/');
    const spotId = pathParts[pathParts.length - 1];
    this.spotId = spotId;

    if(!this.spotId){
      this.$message.error('无效的ID');
      this.$router.push('/');
      return
    }
    this.fetchSpotData(this.spotId);
    this.fetchSpotRoutes(this.spotId);
    this.fetchUserIdentity();
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
</style> 