module Day03 where

import Util (debug)


-- NB: every bottom right tile is an odd square: 1^2, 3^3, 5^2, etc.
-- Places: (0,0), (1,1), (2,2), so (odd - 1 / 2, odd - 1 / 2)
-- So first find the closest odd square

x1 = sqrt 289326 -- 537.8903233931617
x2 = 537 ^ 2 -- 288369, so at position (537 - 1 / 2, 537 - 1 / 2) == (268,268)
diff = input - x2 -- 957
--    957                  956                   418             
-- (268,268) --1 right-> (269,268) --538 up-> (269,-270) --418 left-> (-149,-270)
-- so the distance is == 419

input = 289326 :: Int

part1 :: Int
part1 = let
  -- TODO: handle even case
  oddSqrt = floor (sqrt (fromIntegral input :: Float))
  posX = (oddSqrt - 1) `div` 2
  posY = posX
  diff = input - (oddSqrt ^ 2)
  -- one to the right if possible
  (right,diff') = (min diff 1,max 0 diff - 1)
  posX' = posX + right
  -- oddSqrt + 1 up if possible
  maxUpLeft = oddSqrt + 1
  (up, diff'') = if (diff' - maxUpLeft) >= 0
                 then (oddSqrt + 1, diff' - maxUpLeft)
                 else (diff',0)
  posY' = posY - up
  -- move left with whatever is left
  posX'' = posX' - diff''
  in abs posX'' + abs posY' `debug` (show posX'' ++ "," ++ show posY') 
