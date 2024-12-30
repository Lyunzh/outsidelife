import OpenAI from "openai";
// import Constants from './constants';

// 创建 OpenAI 实例
const openai = new OpenAI({
  apiKey: "sk-d9edb991c8e24a9da544d4aa49a8b188",
  baseURL: "https://dashscope.aliyuncs.com/compatible-mode/v1",
  dangerouslyAllowBrowser: true
});

export async function chatWithAI(message, history = []) {
  try {
    const completion = await openai.chat.completions.create({
      model: "qwen-turbo",
      messages: [
        { role: "system", content: "You are a helpful assistant." },
        ...history.map(msg => ({
          role: msg.role,
          content: msg.content
        })),
        { role: "user", content: message }
      ]
    });

    return {
      data: {
        content: completion.choices[0].message.content || '抱歉，我暂时无法回答这个问题'
      }
    };
  } catch (error) {
    console.error('AI API Error:', error);
    if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
      throw new Error('网络连接失败，请检查您的网络设置');
    }
    throw error;
  }
} 