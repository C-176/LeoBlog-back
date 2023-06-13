-- 商品库存key
local stockKey = KEYS[1]
-- 如果成功扣减库存，返回2，并且将徽章的持有人集合添加元素:用户ID
local badgeId = ARGV[1]
local userId = ARGV[2]

local result =  redis.call('get', stockKey)
if(type(result) == 'nil')
then
    return 0
end
if(tonumber(result) <= 0)
then
    return 1
end
redis.call('decr', stockKey)
redis.call('sadd', 'badge:owner:'..badgeId,userId)
return 2