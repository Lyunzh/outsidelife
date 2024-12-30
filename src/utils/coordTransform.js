export function parseLocation(locationStr) {
  if (Array.isArray(locationStr)) return locationStr;
  if (!locationStr) return [121.215252, 31.286054]; // 默认坐标
  
  try {
    return locationStr.split(',').map(num => Number(num.trim()));
  } catch (error) {
    console.error('坐标转换失败:', error);
    return [121.215252, 31.286054]; // 转换失败时返回默认坐标
  }
}
