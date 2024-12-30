<template>
  <div class="ai-chat-container" :class="{ 'chat-expanded': isExpanded }">
    <!-- 聊天图标按钮 -->
    <div class="chat-toggle" @click="toggleChat">
      <div v-if="!isExpanded" class="ai-icon">
        <i class="el-icon-chat-line-round"></i>
        <div class="ai-badge">AI</div>
        <div class="pulse-ring"></div>
      </div>
      <div v-else class="exit-button">
        <i class="el-icon-close exit-icon"></i>
      </div>
    </div>

    <!-- 聊天窗口 -->
    <div v-show="isExpanded" class="chat-window">
      <div class="chat-header">
        <div class="header-content">
          <div class="header-left">
            <i class="el-icon-chat-line-round"></i>
            <span>智能旅游建议小助手</span>
            <div class="status-dot"></div>
          </div>
          <div class="header-right">
            <div class="exit-circle" @click.stop="closeChat">
              <i class="el-icon-close"></i>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-messages" ref="messageContainer" @scroll="handleScroll">
        <div v-for="(message, index) in messages" 
             :key="index" 
             :class="['message', message.role, {'slide-in': message.isNew}]">
          <div class="message-content">{{ message.content }}</div>
        </div>
      </div>

      <div class="chat-input-container">
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
  props: {
    context: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      isExpanded: false,
      inputMessage: '',
      isLoading: false,
      messages: [
        {
          role: "assistant",
          content: this.getWelcomeMessage()
        }
      ]
    }
  },
  methods: {
    getWelcomeMessage() {
      if (this.context.type === 'route') {
        return `您好！我是智能旅游建议小助手。我可以为您解答关于"${this.context.routeName}"路线的任何问题。`;
      } else if (this.context.type === 'spot') {
        return `您好！我是智能旅游建议小助手。我可以为您解答关于"${this.context.spotName}"景点的任何问题。`;
      }
      return "您好！我是智能旅游建议小助手，有什么可以帮您的吗？";
    },
    
    async sendMessage() {
      if (!this.inputMessage.trim() || this.isLoading) return;

      const userMessage = {
        role: "user",
        content: this.inputMessage,
        isNew: true
      };
      this.messages.push(userMessage);

      const messageHistory = this.messages.slice(1);
      this.inputMessage = '';
      this.isLoading = true;

      try {
        // 添加上下文信息到消息历史
        const contextMessage = this.getContextMessage();
        const messagesWithContext = contextMessage ? [contextMessage, ...messageHistory] : messageHistory;
        
        const response = await chatWithAI(userMessage.content, messagesWithContext);
        this.messages.push({
          role: "assistant",
          content: response.data.content,
          isNew: true
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

    getContextMessage() {
      if (!this.context.type) return null;

      let contextContent = '';
      if (this.context.type === 'route') {
        contextContent = `当前正在查看路线"${this.context.routeName}"。路线描述：${this.context.description}`;
      } else if (this.context.type === 'spot') {
        contextContent = `当前正在查看景点"${this.context.spotName}"。景点描述：${this.context.description}`;
      }

      return contextContent ? {
        role: "system",
        content: contextContent
      } : null;
    },
    
    toggleChat() {
      this.isExpanded = !this.isExpanded;
      if (this.isExpanded) {
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    closeChat() {
      this.isExpanded = false;
    },
    scrollToBottom() {
      const container = this.$refs.messageContainer;
      container.scrollTop = container.scrollHeight;
    },
    handleScroll({ target }) {
      const { scrollTop } = target;
      
      if (scrollTop === 0) {
        this.loadHistoryMessages();
      }
    },
    loadHistoryMessages() {
      console.log('Loading history messages...');
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
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.15));
}

.chat-toggle {
  width: 60px;
  height: 60px;
  border-radius: 30px 0 0 30px;
  background: linear-gradient(135deg, #2B32B2, #1488CC);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.ai-icon {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-icon i {
  font-size: 28px;
  z-index: 2;
}

.ai-badge {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #FF4D4F;
  color: white;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: bold;
  z-index: 2;
}

.pulse-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0;
  }
  100% {
    transform: scale(0.95);
    opacity: 0.5;
  }
}

.chat-window {
  position: absolute;
  top: 50%;
  right: 70px;
  transform: translateY(-50%);
  width: 450px;
  height: 600px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 20px;
  background: linear-gradient(135deg, #2B32B2, #1488CC);
  color: white;
  border-radius: 16px 16px 0 0;
  position: relative;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #52C41A;
  border-radius: 50%;
  position: relative;
}

.status-dot::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: inherit;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.exit-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.exit-circle:hover {
  background: #FF4D4F;
  transform: rotate(90deg);
}

.exit-circle i {
  font-size: 20px;
  color: white;
}

.message {
  max-width: 85%;
  padding: 12px 16px;
  border-radius: 16px;
  margin-bottom: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.message.user {
  background: linear-gradient(135deg, #2B32B2, #1488CC);
  color: white;
  margin-left: 20%;
  box-shadow: 0 4px 12px rgba(43, 50, 178, 0.2);
}

.message.assistant {
  background: white;
  color: #333;
  margin-right: 20%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.chat-input-container {
  position: relative;
  flex-shrink: 0;
  padding: 15px;
  background-color: white;
  border-top: 1px solid #eee;
  border-radius: 0 0 16px 16px;
  z-index: 2;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
}

.chat-input-container .el-input {
  margin-bottom: 0;
}

/* 修改展开状态的按钮样式 */
.chat-expanded .chat-toggle {
  width: 50px;  /* 减小宽度使其更像一个图标 */
  height: 50px;  /* 保持正方形 */
  border-radius: 25px 0 0 25px;  /* 保持半圆形 */
  background: #F56C6C;
  transition: all 0.3s ease;
}

.chat-expanded .chat-toggle:hover {
  width: 90px;
  background: #f78989;
}

.chat-expanded .chat-toggle i {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.chat-expanded .chat-toggle:hover i {
  transform: rotate(90deg);
}

@media (max-width: 768px) {
  .chat-expanded .chat-toggle {
    width: 70px;
  }
  
  .chat-window {
    width: calc(100vw - 90px);
    height: 80vh;
    right: 80px;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(144, 147, 153, 0.3);
  border-radius: 3px;
  transition: background 0.3s;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(144, 147, 153, 0.5);
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

.exit-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #F56C6C;
  border-radius: 20px 0 0 20px;
  transition: all 0.3s ease;
}

.exit-button:hover {
  background-color: #ff4d4f;
  transform: scale(1.05);
}

.exit-icon {
  font-size: 24px;
  color: white;
  transform: scale(1.2);
  transition: all 0.3s ease;
}

.exit-button:hover .exit-icon {
  transform: scale(1.4) rotate(180deg);
}

/* 消息滑入动画 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.slide-in {
  animation: slideIn 0.3s ease forwards;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: #f5f7fa;
  min-height: 0;
}

/* 消息容器的滚动阴影效果 */
.chat-messages::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 20px;
  background: linear-gradient(to top, rgba(245, 247, 250, 1), rgba(245, 247, 250, 0));
  pointer-events: none;
}
</style> 