const API_KEY = process.env.VUE_APP_DASHSCOPE_API_KEY;
const API_URL = 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation';

export async function chatWithAI(message, history = []) {
  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${API_KEY}`,
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'X-DashScope-SSE': 'disable'
      },
      mode: 'cors',
      credentials: 'omit',
      body: JSON.stringify({
        model: "qwen-turbo",
        input: {
          messages: [
            { role: "system", content: "You are a helpful assistant." },
            ...history.map(msg => ({
              role: msg.role,
              content: msg.content
            })),
            { role: "user", content: message }
          ]
        }
      })
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'API request failed');
    }

    const data = await response.json();
    if (data.error) {
      throw new Error(data.error.message || 'API response error');
    }

    return {
      data: {
        content: data.output?.text || '抱歉，我暂时无法回答这个问题'
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