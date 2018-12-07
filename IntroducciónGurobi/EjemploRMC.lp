\ LP format - for model browsing. Use MPS format to capture full model detail.
Minimize
  2 x(0,1) + 8 x(0,2) + 3 x(1,2) + 7 x(1,3) + x(1,4) + 10 x(2,3) + 2 x(3,4)
   + 6 x(2,5) + 3 x(3,5) + 5 x(4,5) + 12 x(5,6) + 9 x(4,6)
Subject To
 Balance_0: x(0,1) + x(0,2) = 1
 Balance_1: - x(0,1) + x(1,2) + x(1,3) + x(1,4) = 0
 Balance_2: - x(0,2) - x(1,2) + x(2,3) + x(2,5) = 0
 Balance_3: - x(1,3) - x(2,3) + x(3,4) + x(3,5) = 0
 Balance_4: - x(1,4) - x(3,4) + x(4,5) + x(4,6) = 0
 Balance_5: - x(2,5) - x(3,5) - x(4,5) + x(5,6) = 0
 Balance_6: - x(5,6) - x(4,6) = -1
Bounds
Binaries
 x(0,1) x(0,2) x(1,2) x(1,3) x(1,4) x(2,3) x(3,4) x(2,5) x(3,5) x(4,5)
 x(5,6) x(4,6)
End
