<template>
  <div class="ai-chat-container" :class="{ 'chat-expanded': isExpanded }">
    <!-- 聊天图标/最小化按钮 -->
    <div class="chat-toggle" @click="toggleChat">
      <div v-if="!isExpanded" class="ai-icon">
        <i class="el-icon-chat-line-round"></i>
        <span class="ai-badge">AI</span>
      </div>
      <i v-else class="el-icon-minus"></i>
    </div>

    <!-- 聊天窗口 -->
    <div v-show="isExpanded" class="chat-window">
      <div class="chat-header">
        <div class="header-content">
          <div class="header-left">
            <i class="el-icon-chat-line-round"></i>
            <span>通义千问 AI 助手</span>
          </div>
          <div class="header-right">
            <span class="ai-tag">AI</span>
            <i class="el-icon-minus minimize-button" @click.stop="toggleChat"></i>
            <i class="el-icon-close close-button" @click.stop="closeChat"></i>
          </div>
        </div>
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
    closeChat() {
      this.isExpanded = false;
      this.messages = [
        {
          role: "assistant",
          content: "您好！我是通义千问助手，有什么可以帮您的吗？"
        }
      ];
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
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1000;
}

.chat-toggle {
  width: 60px;
  height: 60px;
  border-radius: 30px 0 0 30px; /* 修改为半圆形 */
  background: linear-gradient(135deg, #409EFF, #007FFF);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: -2px 0 12px rgba(0, 127, 255, 0.3);
  transition: all 0.3s ease;
  cursor: pointer;
}

.chat-toggle:hover {
  width: 70px; /* 悬浮时稍微展开 */
  box-shadow: -4px 0 16px rgba(0, 127, 255, 0.4);
}

.ai-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.ai-icon i {
  font-size: 28px;
  opacity: 0.9;
}

.ai-badge {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #F56C6C;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.4);
  border: 2px solid white;
  z-index: 1;
}

.chat-window {
  position: absolute;
  top: 50%;
  right: 70px; /* 调整位置，与按钮有一定间距 */
  transform: translateY(-50%);
  width: 350px;
  height: 500px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-50%) translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateY(-50%) translateX(0);
  }
}

.chat-header {
  padding: 15px;
  background: linear-gradient(135deg, #409EFF, #007FFF);
  color: white;
  border-radius: 8px 8px 0 0;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 0 5px;
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 35px; /* 为关闭按钮留出空间 */
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.ai-tag {
  background-color: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.minimize-button,
.close-button {
  font-size: 18px;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.minimize-button:hover,
.close-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.close-button:hover {
  transform: rotate(90deg);
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

/* 修改展开状态的按钮样式 */
.chat-expanded .chat-toggle {
  width: 40px;
  height: 40px;
  border-radius: 20px 0 0 20px;
  background: #F56C6C;
}

.chat-expanded .chat-toggle i {
  font-size: 20px;
}

@media (max-width: 768px) {
  .chat-window {
    width: calc(100vw - 80px); /* 减去按钮宽度和一些间距 */
    right: 70px;
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

/* 确保聊天窗口在拖动时不会移动 */
.chat-window {
  position: absolute;
  bottom: 60px;
  right: 0;
  transform-origin: bottom right;
  pointer-events: auto;
}

/* 修改容器样式 */
.ai-chat-container {
  position: fixed;
  z-index: 1000;
  pointer-events: none; /* 防止干扰拖动 */
}

.ai-chat-container > * {
  pointer-events: auto; /* 恢复子元素的事件 */
}
</style> 