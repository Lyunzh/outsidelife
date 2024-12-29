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
      <div v-for="spot in spots" :key="spot.spotId" class="spot-card" @click="goToSpot(spot.spotId)">
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
import { getSpots,getHikeSpots,getBikeSpots } from '@/apis/forum'
import { basePicturesPath } from '@/utils/alldata';

export default {

  name: 'HomeView',
  data(){
    return{
      spots:[]
      

      
    }
  },
  methods:{
    async getSpots(){
      const response = await getSpots()
      this.spots=response.data.data
      for(let one of this.spots){
        one.imageUrl=basePicturesPath+one.imageUrl
      }
    },
    goToSpot(spotId) {
      this.$router.push(`/spot/${spotId}`)
    },
    async goToHike(){
      const response = await getHikeSpots()
      this.spots=response.data.data
      for(let one of this.spots){
        one.imageUrl=basePicturesPath+one.imageUrl
      }
    },
    async goToBike(){
      const response = await getBikeSpots()
      this.spots=response.data.data
      for(let one of this.spots){
        one.imageUrl=basePicturesPath+one.imageUrl
      }
    }
  },
  async mounted() {
    await this.getSpots()
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