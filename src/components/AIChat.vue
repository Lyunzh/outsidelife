<template>
  <div class="ai-chat-container" :class="{ 'chat-expanded': isExpanded }">
    <!-- 聊天图标/最小化按钮 -->
    <div class="chat-toggle" @click="toggleChat">
      <i :class="isExpanded ? 'el-icon-close' : 'el-icon-chat-line-round'"></i>
    </div>

    <!-- 聊天窗口 -->
    <div v-show="isExpanded" class="chat-window">
      <div class="chat-header">
        <span>通义千问助手</span>
      </div>
      
      <div class="chat-messages" ref="messageContainer">
        <div v-for="(message, index) in messages" 
             :key="index" 
             :class="['message', message.role]">
          <div class="message-content">{{ message.content }}</div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          placeholder="请输入您的问题..."
          :disabled="isLoading"
          @keyup.enter="sendMessage"
        >
          <template #append>
            <el-button 
              :loading="isLoading"
              @click="sendMessage">
              发送
            </el-button>
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script>
import { chatWithAI } from '@/apis/ai';

export default {
  name: 'AIChat',
  data() {
    return {
      isExpanded: false,
      inputMessage: '',
      isLoading: false,
      messages: [
        {
          role: "assistant",
          content: "您好！我是通义千问助手，有什么可以帮您的吗？"
        }
      ]
    }
  },
  methods: {
    toggleChat() {
      this.isExpanded = !this.isExpanded;
    },

    async sendMessage() {
      if (!this.inputMessage.trim() || this.isLoading) return;

      const userMessage = {
        role: "user",
        content: this.inputMessage
      };
      this.messages.push(userMessage);

      const messageHistory = this.messages.slice(1);  // 排除欢迎消息
      this.inputMessage = '';
      this.isLoading = true;

      try {
        const response = await chatWithAI(userMessage.content, messageHistory);
        this.messages.push({
          role: "assistant",
          content: response.data.content
        });
      } catch (error) {
        console.error('Chat error:', error);
        this.$message.error(error.message || '获取回答失败，请稍后重试');
      } finally {
        this.isLoading = false;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },

    scrollToBottom() {
      const container = this.$refs.messageContainer;
      container.scrollTop = container.scrollHeight;
    }
  }
}
</script>

<style scoped>
.ai-chat-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.chat-toggle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.chat-toggle:hover {
  transform: scale(1.1);
}

.chat-toggle i {
  font-size: 24px;
}

.chat-window {
  position: absolute;
  bottom: 60px;
  right: 0;
  width: 350px;
  height: 500px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.chat-header {
  padding: 15px;
  background: #409EFF;
  color: white;
  border-radius: 8px 8px 0 0;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: #f5f7fa;
}

.message {
  max-width: 80%;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 10px;
  word-wrap: break-word;
}

.message.user {
  align-self: flex-end;
  background-color: #409EFF;
  color: white;
  margin-left: 20%;
}

.message.assistant {
  align-self: flex-start;
  background-color: white;
  color: #333;
  margin-right: 20%;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #eee;
  background-color: white;
  border-radius: 0 0 8px 8px;
}

.chat-input .el-input {
  margin-bottom: 0;
}

.chat-expanded .chat-toggle {
  position: absolute;
  top: -40px;
  right: 0;
  background-color: #f56c6c;
}

.chat-expanded .chat-toggle:hover {
  background-color: #f78989;
}

/* 添加动画效果 */
.chat-window {
  transform-origin: bottom right;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-window {
    width: 90vw;
    right: 5vw;
  }
  
  .chat-toggle {
    right: 5vw;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f5f7fa;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #909399;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #606266;
}
</style> 