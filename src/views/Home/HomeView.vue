<template>
  <div class="home">
    <el-card class="category-tag" shadow="hover">
      <button @click="goToBike">
      <img src="../../assets/pictures/CategoryPicture/Bike.jpg"  class="category-image" style="width: 100%;"/>
      </button>
    </el-card>
    <el-card class="category-tag" shadow="hover">
      <button @click="goToHike">
      <img src="../../assets/pictures/CategoryPicture/Hike.jpg"  class="category-image" style="width: 100%;"/>
      </button>
    </el-card>
    <el-card class="spots-list">
      <div :v-for="spot in spots" :key="spot.id" class="spot-card" @click="goToSpot(spot.spotId)">
        <h3>{{ spot.spotName }}</h3>
        <p class="spot-preview">{{ spot.description?.substring(0, 20) || '' }}...</p>
        <div class="spot-meta">
          <span>{{ spot.location }}</span>
        </div>
      </div>
    </el-card> 

  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSpots,getHikeSpots,getBikeSpots } from '@/apis/forum'
import { basePicturesPath } from '@/utils/alldata';

export default {
  name: 'HomeView',
  setup() {
    const router = useRouter()
    const spots = ref([])

    // 获取数据的方法
    const fetchData = async () => {
      try {
        // 并行请求数据
        const [spotres] = await Promise.all([
          getSpots()
        ])
      
        spots.value = spotres.data.data

        for(let one of spots.value){
          one.imageUrl=basePicturesPath+one.imageUrl
        }

        console.log('Posts loaded:', spots.value)
      } catch (error) {
        console.error('获取数据失败:', error)
        // 这里可以添加错误处理，比如显示错误提示
      }
    }

    onMounted(() => {
      fetchData()
    })

    const goToSpot = (spotId) => {
      router.push(`/spot/${spotId}`)
    }

    const goToHike = async () => {
      try {
        const [spotres] = await Promise.all([
          getHikeSpots()
        ])
      
        spots.value = spotres.data.data

        for(let one of spots.value){
          one.imageUrl=basePicturesPath+one.imageUrl
        }

        console.log('Posts loaded:', spots.value)
      } catch (error) {
        console.error('获取数据失败:', error)
        // 这里可以添加错误处理，比如显示错误提示
      }
    }

    const goToBike = async () => {
      try {
        const [spotres] = await Promise.all([
          getBikeSpots()
        ])
      
        spots.value = spotres.data.data

        for(let one of spots.value){
          one.imageUrl=basePicturesPath+one.imageUrl
        }

        console.log('Posts loaded:', spots.value)
      } catch (error) {
        console.error('获取数据失败:', error)
        // 这里可以添加错误处理，比如显示错误提示
      }
    }

    return {
      spots,
      goToSpot,
      goToHike,
      goToBike
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  justify-content: center;
}

.search-bar {
  margin-bottom: 20px;
}

.categories {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.posts-list {
  display: grid;
  gap: 20px;
}

.post-card {
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.9em;
}

.category-tag {
  width: 30%;
  margin: 0;
}

.category-image {
  width: 50% !important;
  height: auto;
  display: block;
  margin: auto;
}

</style> 