<template>
  <div class="home">
    <!-- 分类区域 -->
    <div class="categories-container">
      <div class="category-tag" @click="goToBike">
        <div class="category-image-container">
          <img src="../../assets/pictures/CategoryPicture/Bike.jpg" class="category-image" />
          <div class="category-overlay">
            <i class="el-icon-bicycle"></i>
          </div>
        </div>
        <div class="category-title">骑行路线</div>
      </div>
      <div class="category-tag" @click="goToHike">
        <div class="category-image-container">
          <img src="../../assets/pictures/CategoryPicture/Hike.jpg" class="category-image" />
          <div class="category-overlay">
            <i class="el-icon-position"></i>
          </div>
        </div>
        <div class="category-title">徒步路线</div>
      </div>
    </div>

    <!-- 景点列表 -->
    <el-card class="spots-list">
      <div v-for="spot in spots" :key="spot.spotId" class="spot-card" @click="goToSpot(spot.spotId)">
        <h3>{{ spot.spotName }}</h3>
        <p class="spot-preview">{{ spot.description?.substring(0, 20) || '' }}...</p>
      </div>
    </el-card>

    <!-- 地图容器 -->
    <el-card class="map-container">
      <div class="map-header">
        <h2>景点地图</h2>
      </div>
      <div id="container"></div>
    </el-card>
  </div>
</template>

<script>
import { getSpots,getHikeSpots,getBikeSpots } from '@/apis/forum'
import { basePicturesPath } from '@/utils/alldata';
import AMapLoader from "@amap/amap-jsapi-loader";
import { defineComponent } from "vue";
import { parseLocation } from '@/utils/coordTransform';

export default defineComponent({
  name: 'HomeView',
  data(){
    return{
      spots:[],
      markers: [],
      map: null,
      mockSpots: [
        {
          spotId: 1,
          spotName: "古猗园",
          description: "古猗园是上海市著名的古典园林，位于松江区，建于明代...",
          imageUrl: "https://a.amap.com/jsapi_demos/static/resource/img/men3.png",
          category: "徒步",
          location: "121.212401, 31.282552"
        },
        {
          spotId: 2,
          spotName: "佘山",
          description: "佘山是上海市最高峰，海拔100米，是著名的登山胜地...",
          imageUrl: "https://a.amap.com/jsapi_demos/static/resource/img/tingzi.png",
          category: "徒步",
          location: "121.218022, 31.280645"
        },
        {
          spotId: 3,
          spotName: "滨江森林公园",
          description: "滨江森林公园是上海市最大的生态型森林公园，占地面积1000公顷...",
          imageUrl: "https://a.amap.com/jsapi_demos/static/resource/img/trees.png",
          category: "骑行",
          location: "121.21748, 31.285429"
        }
      ]
    }
  },
  methods:{
    async getSpots(){
      try {
        const response = await getSpots();
        this.spots = response.data.data;
        for(let one of this.spots){
          
          one.imageUrl = basePicturesPath + one.imageUrl;
        }
        await this.initMap();
      } catch(error) {
        this.$message.error('获取景点数据失败');
        // 使用模拟数据
        this.spots = this.mockSpots;
        await this.initMap();
      }
    },

    // 初始化地图
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
          zooms: [14, 20],
          center: [121.215252, 31.286054],
        });

        this.markers = [];  // 清空标记数组

        // 为每个景点添加标记
        this.spots.forEach(spot => {
          console.log('Creating marker for:', spot.spotName);
          const position = parseLocation(spot.location);
          const elasticMarker = new AMap.ElasticMarker({
            position: position,
            styles: [{
              icon: {
                img: spot.imageUrl,
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
                content: spot.spotName,
                position: "BM",
                minZoom: 15,
              },
            }],
            zoomStyleMapping: { 14: 0, 15: 0, 16: 0, 17: 0, 18: 0, 19: 0, 20: 0 },
          });

          // 添加点击事件
          elasticMarker.on("click", () => {
            const infoWindow = new AMap.InfoWindow({
              content: `<div style="padding:10px;">
                         <h4>${spot.spotName}</h4>
                         <p>${spot.description}</p>
                         <div style="
                           margin-top: 10px;
                           padding-top: 10px;
                           border-top: 1px solid #eee;
                           text-align: right;
                         ">
                           <button onclick="document.dispatchEvent(new CustomEvent('goToSpot', {detail: {spotId: ${spot.spotId}}}))"
                             style="
                               background-color: #409EFF;
                               color: white;
                               border: none;
                               padding: 5px 15px;
                               border-radius: 4px;
                               cursor: pointer;
                               font-size: 14px;
                             "
                           >查看景点</button>
                         </div>
                       </div>`,
              offset: new AMap.Pixel(0, -30),
              size: new AMap.Size(300, 160)
            });
            infoWindow.open(this.map, elasticMarker.getPosition());
          });

          this.markers.push(elasticMarker);
        });

        // 添加自定义事件监听器
        document.addEventListener('goToSpot', (event) => {
          this.goToSpot(event.detail.spotId);
        });

        // 将所有标记添加到地图
        if (this.markers.length > 0) {
          this.map.add(this.markers);
          this.map.setFitView();
        }

      } catch (error) {
        console.error('地图初始化失败:', error);
      }
    },

    goToSpot(spotId) {
      this.$router.push(`/spot/${spotId}`)
    },

    async goToHike(){
      try {
        const response = await getHikeSpots();
        this.spots = response.data.data;
        for(let one of this.spots){
          one.imageUrl = basePicturesPath + one.imageUrl;
        }
        await this.initMap();
      } catch(error) {
        this.$message.error('获取徒步景点数据失败');
        // 使用模拟数据
        this.spots = this.mockSpots.filter(spot => spot.category === "徒步");
        await this.initMap();
      }
    },

    async goToBike(){
      try {
        const response = await getBikeSpots();
        this.spots = response.data.data;
        for(let one of this.spots){
          one.imageUrl = basePicturesPath + one.imageUrl;
        }
        await this.initMap();
      } catch(error) {
        this.$message.error('获取骑行景点数据失败');
        // 使用模拟数据
        this.spots = this.mockSpots.filter(spot => spot.category === "骑行");
        await this.initMap();
      }
    }
  },

  async mounted() {
    await this.getSpots()
  },

  beforeUnmount() {
    if (this.map) {
      if (this.markers && this.markers.length > 0) {
        this.map.remove(this.markers);
        this.markers = [];
      }
      this.map.destroy();
      this.map = null;
    }
  }
})
</script>

<style scoped>
.home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 分类区域样式 */
.categories-container {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px 0;
}

.category-tag {
  width: 200px;
  transition: all 0.3s ease;
  cursor: pointer;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-tag:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.category-image-container {
  position: relative;
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.category-image {
  width: 100%;
  height: 100%;
  display: block;
  margin: auto;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.category-tag:hover .category-image {
  transform: scale(1.1);
}

.category-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0,0,0,0.2), rgba(0,0,0,0.5));
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.category-tag:hover .category-overlay {
  opacity: 1;
}

.category-overlay i {
  font-size: 40px;
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.category-title {
  text-align: center;
  padding: 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  background: #fff;
}

/* 景点列表样式 */
.spots-list {
  grid-column: 1 / -1;
  width: 100%;
  padding: 20px;
}

.spot-card {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 15px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.spot-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.spot-card h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.spot-preview {
  color: #666;
  margin: 0;
  line-height: 1.5;
}

/* 地图容器样式 */
.map-container {
  width: 100%;
  padding: 20px;
}

.map-header {
  margin-bottom: 20px;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.map-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

#container {
  width: 100%;
  height: 500px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}
</style> 