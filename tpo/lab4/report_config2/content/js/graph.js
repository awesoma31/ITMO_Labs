/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
$(document).ready(function() {

    $(".click-title").mouseenter( function(    e){
        e.preventDefault();
        this.style.cursor="pointer";
    });
    $(".click-title").mousedown( function(event){
        event.preventDefault();
    });

    // Ugly code while this script is shared among several pages
    try{
        refreshHitsPerSecond(true);
    } catch(e){}
    try{
        refreshResponseTimeOverTime(true);
    } catch(e){}
    try{
        refreshResponseTimePercentiles();
    } catch(e){}
});


var responseTimePercentilesInfos = {
        data: {"result": {"minY": 907.0, "minX": 0.0, "maxY": 1246.0, "series": [{"data": [[0.0, 907.0], [0.1, 912.0], [0.2, 919.0], [0.3, 922.0], [0.4, 924.0], [0.5, 929.0], [0.6, 936.0], [0.7, 946.0], [0.8, 952.0], [0.9, 954.0], [1.0, 959.0], [1.1, 960.0], [1.2, 962.0], [1.3, 966.0], [1.4, 968.0], [1.5, 969.0], [1.6, 970.0], [1.7, 970.0], [1.8, 970.0], [1.9, 970.0], [2.0, 970.0], [2.1, 970.0], [2.2, 970.0], [2.3, 970.0], [2.4, 970.0], [2.5, 970.0], [2.6, 970.0], [2.7, 970.0], [2.8, 970.0], [2.9, 970.0], [3.0, 970.0], [3.1, 971.0], [3.2, 971.0], [3.3, 971.0], [3.4, 971.0], [3.5, 971.0], [3.6, 971.0], [3.7, 971.0], [3.8, 971.0], [3.9, 971.0], [4.0, 971.0], [4.1, 971.0], [4.2, 971.0], [4.3, 971.0], [4.4, 971.0], [4.5, 971.0], [4.6, 971.0], [4.7, 971.0], [4.8, 971.0], [4.9, 971.0], [5.0, 971.0], [5.1, 971.0], [5.2, 971.0], [5.3, 971.0], [5.4, 971.0], [5.5, 971.0], [5.6, 971.0], [5.7, 971.0], [5.8, 971.0], [5.9, 971.0], [6.0, 971.0], [6.1, 971.0], [6.2, 971.0], [6.3, 971.0], [6.4, 971.0], [6.5, 971.0], [6.6, 971.0], [6.7, 971.0], [6.8, 971.0], [6.9, 971.0], [7.0, 971.0], [7.1, 971.0], [7.2, 971.0], [7.3, 971.0], [7.4, 972.0], [7.5, 972.0], [7.6, 972.0], [7.7, 972.0], [7.8, 972.0], [7.9, 972.0], [8.0, 972.0], [8.1, 972.0], [8.2, 972.0], [8.3, 972.0], [8.4, 972.0], [8.5, 972.0], [8.6, 972.0], [8.7, 972.0], [8.8, 972.0], [8.9, 972.0], [9.0, 972.0], [9.1, 972.0], [9.2, 972.0], [9.3, 972.0], [9.4, 972.0], [9.5, 972.0], [9.6, 972.0], [9.7, 972.0], [9.8, 972.0], [9.9, 972.0], [10.0, 972.0], [10.1, 972.0], [10.2, 972.0], [10.3, 972.0], [10.4, 972.0], [10.5, 972.0], [10.6, 972.0], [10.7, 972.0], [10.8, 972.0], [10.9, 972.0], [11.0, 972.0], [11.1, 972.0], [11.2, 972.0], [11.3, 972.0], [11.4, 972.0], [11.5, 972.0], [11.6, 972.0], [11.7, 972.0], [11.8, 972.0], [11.9, 972.0], [12.0, 972.0], [12.1, 972.0], [12.2, 972.0], [12.3, 972.0], [12.4, 972.0], [12.5, 972.0], [12.6, 972.0], [12.7, 972.0], [12.8, 972.0], [12.9, 972.0], [13.0, 972.0], [13.1, 972.0], [13.2, 972.0], [13.3, 972.0], [13.4, 973.0], [13.5, 973.0], [13.6, 973.0], [13.7, 973.0], [13.8, 973.0], [13.9, 973.0], [14.0, 973.0], [14.1, 973.0], [14.2, 973.0], [14.3, 973.0], [14.4, 973.0], [14.5, 973.0], [14.6, 973.0], [14.7, 973.0], [14.8, 973.0], [14.9, 973.0], [15.0, 973.0], [15.1, 973.0], [15.2, 973.0], [15.3, 973.0], [15.4, 973.0], [15.5, 973.0], [15.6, 973.0], [15.7, 973.0], [15.8, 973.0], [15.9, 973.0], [16.0, 973.0], [16.1, 973.0], [16.2, 973.0], [16.3, 973.0], [16.4, 973.0], [16.5, 973.0], [16.6, 973.0], [16.7, 973.0], [16.8, 973.0], [16.9, 973.0], [17.0, 973.0], [17.1, 973.0], [17.2, 973.0], [17.3, 973.0], [17.4, 973.0], [17.5, 973.0], [17.6, 973.0], [17.7, 973.0], [17.8, 973.0], [17.9, 973.0], [18.0, 973.0], [18.1, 973.0], [18.2, 973.0], [18.3, 973.0], [18.4, 973.0], [18.5, 973.0], [18.6, 973.0], [18.7, 973.0], [18.8, 973.0], [18.9, 974.0], [19.0, 974.0], [19.1, 974.0], [19.2, 974.0], [19.3, 974.0], [19.4, 974.0], [19.5, 974.0], [19.6, 974.0], [19.7, 974.0], [19.8, 974.0], [19.9, 974.0], [20.0, 974.0], [20.1, 974.0], [20.2, 974.0], [20.3, 974.0], [20.4, 974.0], [20.5, 974.0], [20.6, 974.0], [20.7, 974.0], [20.8, 974.0], [20.9, 974.0], [21.0, 974.0], [21.1, 974.0], [21.2, 974.0], [21.3, 974.0], [21.4, 974.0], [21.5, 974.0], [21.6, 974.0], [21.7, 974.0], [21.8, 974.0], [21.9, 974.0], [22.0, 974.0], [22.1, 974.0], [22.2, 974.0], [22.3, 974.0], [22.4, 974.0], [22.5, 974.0], [22.6, 974.0], [22.7, 974.0], [22.8, 974.0], [22.9, 974.0], [23.0, 974.0], [23.1, 974.0], [23.2, 974.0], [23.3, 974.0], [23.4, 974.0], [23.5, 974.0], [23.6, 974.0], [23.7, 975.0], [23.8, 975.0], [23.9, 975.0], [24.0, 975.0], [24.1, 975.0], [24.2, 975.0], [24.3, 975.0], [24.4, 975.0], [24.5, 975.0], [24.6, 975.0], [24.7, 975.0], [24.8, 975.0], [24.9, 975.0], [25.0, 975.0], [25.1, 975.0], [25.2, 975.0], [25.3, 975.0], [25.4, 975.0], [25.5, 975.0], [25.6, 975.0], [25.7, 975.0], [25.8, 975.0], [25.9, 975.0], [26.0, 975.0], [26.1, 975.0], [26.2, 975.0], [26.3, 975.0], [26.4, 975.0], [26.5, 975.0], [26.6, 975.0], [26.7, 975.0], [26.8, 975.0], [26.9, 975.0], [27.0, 975.0], [27.1, 975.0], [27.2, 975.0], [27.3, 975.0], [27.4, 975.0], [27.5, 975.0], [27.6, 976.0], [27.7, 976.0], [27.8, 976.0], [27.9, 976.0], [28.0, 976.0], [28.1, 976.0], [28.2, 976.0], [28.3, 976.0], [28.4, 976.0], [28.5, 976.0], [28.6, 976.0], [28.7, 976.0], [28.8, 976.0], [28.9, 976.0], [29.0, 976.0], [29.1, 976.0], [29.2, 976.0], [29.3, 976.0], [29.4, 976.0], [29.5, 976.0], [29.6, 976.0], [29.7, 976.0], [29.8, 976.0], [29.9, 976.0], [30.0, 976.0], [30.1, 976.0], [30.2, 976.0], [30.3, 976.0], [30.4, 976.0], [30.5, 976.0], [30.6, 976.0], [30.7, 976.0], [30.8, 976.0], [30.9, 976.0], [31.0, 976.0], [31.1, 976.0], [31.2, 976.0], [31.3, 976.0], [31.4, 976.0], [31.5, 976.0], [31.6, 976.0], [31.7, 976.0], [31.8, 976.0], [31.9, 976.0], [32.0, 976.0], [32.1, 976.0], [32.2, 976.0], [32.3, 976.0], [32.4, 976.0], [32.5, 976.0], [32.6, 976.0], [32.7, 976.0], [32.8, 977.0], [32.9, 977.0], [33.0, 977.0], [33.1, 977.0], [33.2, 977.0], [33.3, 977.0], [33.4, 977.0], [33.5, 977.0], [33.6, 977.0], [33.7, 977.0], [33.8, 977.0], [33.9, 977.0], [34.0, 977.0], [34.1, 977.0], [34.2, 977.0], [34.3, 977.0], [34.4, 977.0], [34.5, 977.0], [34.6, 977.0], [34.7, 977.0], [34.8, 977.0], [34.9, 977.0], [35.0, 977.0], [35.1, 977.0], [35.2, 977.0], [35.3, 977.0], [35.4, 977.0], [35.5, 977.0], [35.6, 977.0], [35.7, 977.0], [35.8, 977.0], [35.9, 977.0], [36.0, 977.0], [36.1, 977.0], [36.2, 977.0], [36.3, 977.0], [36.4, 977.0], [36.5, 977.0], [36.6, 977.0], [36.7, 977.0], [36.8, 977.0], [36.9, 978.0], [37.0, 978.0], [37.1, 978.0], [37.2, 978.0], [37.3, 978.0], [37.4, 978.0], [37.5, 978.0], [37.6, 978.0], [37.7, 978.0], [37.8, 978.0], [37.9, 978.0], [38.0, 978.0], [38.1, 978.0], [38.2, 978.0], [38.3, 978.0], [38.4, 978.0], [38.5, 978.0], [38.6, 978.0], [38.7, 978.0], [38.8, 978.0], [38.9, 978.0], [39.0, 978.0], [39.1, 978.0], [39.2, 978.0], [39.3, 978.0], [39.4, 978.0], [39.5, 978.0], [39.6, 978.0], [39.7, 978.0], [39.8, 978.0], [39.9, 978.0], [40.0, 978.0], [40.1, 978.0], [40.2, 978.0], [40.3, 978.0], [40.4, 979.0], [40.5, 979.0], [40.6, 979.0], [40.7, 979.0], [40.8, 979.0], [40.9, 979.0], [41.0, 979.0], [41.1, 979.0], [41.2, 979.0], [41.3, 979.0], [41.4, 979.0], [41.5, 979.0], [41.6, 979.0], [41.7, 979.0], [41.8, 979.0], [41.9, 979.0], [42.0, 979.0], [42.1, 979.0], [42.2, 979.0], [42.3, 979.0], [42.4, 979.0], [42.5, 979.0], [42.6, 979.0], [42.7, 979.0], [42.8, 979.0], [42.9, 979.0], [43.0, 979.0], [43.1, 979.0], [43.2, 979.0], [43.3, 979.0], [43.4, 979.0], [43.5, 979.0], [43.6, 979.0], [43.7, 979.0], [43.8, 979.0], [43.9, 979.0], [44.0, 979.0], [44.1, 979.0], [44.2, 979.0], [44.3, 979.0], [44.4, 980.0], [44.5, 980.0], [44.6, 980.0], [44.7, 980.0], [44.8, 980.0], [44.9, 980.0], [45.0, 980.0], [45.1, 980.0], [45.2, 980.0], [45.3, 980.0], [45.4, 980.0], [45.5, 980.0], [45.6, 980.0], [45.7, 980.0], [45.8, 980.0], [45.9, 980.0], [46.0, 980.0], [46.1, 980.0], [46.2, 980.0], [46.3, 980.0], [46.4, 980.0], [46.5, 980.0], [46.6, 980.0], [46.7, 980.0], [46.8, 980.0], [46.9, 980.0], [47.0, 980.0], [47.1, 980.0], [47.2, 980.0], [47.3, 980.0], [47.4, 980.0], [47.5, 980.0], [47.6, 980.0], [47.7, 980.0], [47.8, 981.0], [47.9, 981.0], [48.0, 981.0], [48.1, 981.0], [48.2, 981.0], [48.3, 981.0], [48.4, 981.0], [48.5, 981.0], [48.6, 981.0], [48.7, 981.0], [48.8, 981.0], [48.9, 981.0], [49.0, 981.0], [49.1, 981.0], [49.2, 981.0], [49.3, 981.0], [49.4, 981.0], [49.5, 981.0], [49.6, 981.0], [49.7, 981.0], [49.8, 981.0], [49.9, 981.0], [50.0, 981.0], [50.1, 981.0], [50.2, 981.0], [50.3, 981.0], [50.4, 981.0], [50.5, 981.0], [50.6, 981.0], [50.7, 981.0], [50.8, 981.0], [50.9, 981.0], [51.0, 982.0], [51.1, 982.0], [51.2, 982.0], [51.3, 982.0], [51.4, 982.0], [51.5, 982.0], [51.6, 982.0], [51.7, 982.0], [51.8, 982.0], [51.9, 982.0], [52.0, 982.0], [52.1, 982.0], [52.2, 982.0], [52.3, 982.0], [52.4, 982.0], [52.5, 982.0], [52.6, 982.0], [52.7, 982.0], [52.8, 982.0], [52.9, 982.0], [53.0, 982.0], [53.1, 982.0], [53.2, 982.0], [53.3, 982.0], [53.4, 982.0], [53.5, 982.0], [53.6, 982.0], [53.7, 982.0], [53.8, 983.0], [53.9, 983.0], [54.0, 983.0], [54.1, 983.0], [54.2, 983.0], [54.3, 983.0], [54.4, 983.0], [54.5, 983.0], [54.6, 983.0], [54.7, 983.0], [54.8, 983.0], [54.9, 983.0], [55.0, 983.0], [55.1, 983.0], [55.2, 983.0], [55.3, 983.0], [55.4, 983.0], [55.5, 983.0], [55.6, 984.0], [55.7, 984.0], [55.8, 984.0], [55.9, 984.0], [56.0, 984.0], [56.1, 984.0], [56.2, 984.0], [56.3, 984.0], [56.4, 984.0], [56.5, 984.0], [56.6, 984.0], [56.7, 984.0], [56.8, 984.0], [56.9, 984.0], [57.0, 984.0], [57.1, 984.0], [57.2, 985.0], [57.3, 985.0], [57.4, 985.0], [57.5, 985.0], [57.6, 985.0], [57.7, 985.0], [57.8, 985.0], [57.9, 985.0], [58.0, 985.0], [58.1, 985.0], [58.2, 985.0], [58.3, 985.0], [58.4, 985.0], [58.5, 985.0], [58.6, 986.0], [58.7, 986.0], [58.8, 986.0], [58.9, 986.0], [59.0, 986.0], [59.1, 986.0], [59.2, 987.0], [59.3, 987.0], [59.4, 987.0], [59.5, 987.0], [59.6, 987.0], [59.7, 987.0], [59.8, 987.0], [59.9, 988.0], [60.0, 988.0], [60.1, 988.0], [60.2, 988.0], [60.3, 989.0], [60.4, 989.0], [60.5, 989.0], [60.6, 989.0], [60.7, 989.0], [60.8, 989.0], [60.9, 990.0], [61.0, 990.0], [61.1, 990.0], [61.2, 990.0], [61.3, 990.0], [61.4, 991.0], [61.5, 991.0], [61.6, 991.0], [61.7, 991.0], [61.8, 991.0], [61.9, 992.0], [62.0, 992.0], [62.1, 992.0], [62.2, 992.0], [62.3, 993.0], [62.4, 993.0], [62.5, 993.0], [62.6, 993.0], [62.7, 993.0], [62.8, 993.0], [62.9, 993.0], [63.0, 993.0], [63.1, 993.0], [63.2, 994.0], [63.3, 994.0], [63.4, 994.0], [63.5, 995.0], [63.6, 995.0], [63.7, 995.0], [63.8, 995.0], [63.9, 996.0], [64.0, 996.0], [64.1, 996.0], [64.2, 996.0], [64.3, 996.0], [64.4, 996.0], [64.5, 997.0], [64.6, 997.0], [64.7, 997.0], [64.8, 997.0], [64.9, 998.0], [65.0, 998.0], [65.1, 998.0], [65.2, 998.0], [65.3, 998.0], [65.4, 998.0], [65.5, 998.0], [65.6, 999.0], [65.7, 999.0], [65.8, 999.0], [65.9, 999.0], [66.0, 999.0], [66.1, 999.0], [66.2, 1000.0], [66.3, 1000.0], [66.4, 1000.0], [66.5, 1000.0], [66.6, 1001.0], [66.7, 1001.0], [66.8, 1001.0], [66.9, 1002.0], [67.0, 1002.0], [67.1, 1002.0], [67.2, 1002.0], [67.3, 1002.0], [67.4, 1002.0], [67.5, 1002.0], [67.6, 1003.0], [67.7, 1003.0], [67.8, 1003.0], [67.9, 1003.0], [68.0, 1003.0], [68.1, 1003.0], [68.2, 1003.0], [68.3, 1003.0], [68.4, 1003.0], [68.5, 1003.0], [68.6, 1003.0], [68.7, 1003.0], [68.8, 1003.0], [68.9, 1003.0], [69.0, 1003.0], [69.1, 1003.0], [69.2, 1003.0], [69.3, 1003.0], [69.4, 1004.0], [69.5, 1004.0], [69.6, 1004.0], [69.7, 1004.0], [69.8, 1004.0], [69.9, 1004.0], [70.0, 1004.0], [70.1, 1004.0], [70.2, 1004.0], [70.3, 1004.0], [70.4, 1004.0], [70.5, 1004.0], [70.6, 1004.0], [70.7, 1004.0], [70.8, 1004.0], [70.9, 1004.0], [71.0, 1004.0], [71.1, 1004.0], [71.2, 1004.0], [71.3, 1004.0], [71.4, 1004.0], [71.5, 1004.0], [71.6, 1004.0], [71.7, 1005.0], [71.8, 1005.0], [71.9, 1005.0], [72.0, 1005.0], [72.1, 1005.0], [72.2, 1005.0], [72.3, 1005.0], [72.4, 1005.0], [72.5, 1005.0], [72.6, 1005.0], [72.7, 1005.0], [72.8, 1005.0], [72.9, 1005.0], [73.0, 1005.0], [73.1, 1005.0], [73.2, 1005.0], [73.3, 1005.0], [73.4, 1006.0], [73.5, 1006.0], [73.6, 1006.0], [73.7, 1006.0], [73.8, 1006.0], [73.9, 1006.0], [74.0, 1006.0], [74.1, 1006.0], [74.2, 1006.0], [74.3, 1007.0], [74.4, 1007.0], [74.5, 1007.0], [74.6, 1007.0], [74.7, 1007.0], [74.8, 1007.0], [74.9, 1007.0], [75.0, 1008.0], [75.1, 1008.0], [75.2, 1008.0], [75.3, 1008.0], [75.4, 1009.0], [75.5, 1009.0], [75.6, 1009.0], [75.7, 1009.0], [75.8, 1009.0], [75.9, 1010.0], [76.0, 1010.0], [76.1, 1010.0], [76.2, 1011.0], [76.3, 1011.0], [76.4, 1011.0], [76.5, 1012.0], [76.6, 1012.0], [76.7, 1012.0], [76.8, 1013.0], [76.9, 1013.0], [77.0, 1013.0], [77.1, 1013.0], [77.2, 1014.0], [77.3, 1014.0], [77.4, 1014.0], [77.5, 1015.0], [77.6, 1015.0], [77.7, 1015.0], [77.8, 1016.0], [77.9, 1016.0], [78.0, 1017.0], [78.1, 1017.0], [78.2, 1017.0], [78.3, 1017.0], [78.4, 1018.0], [78.5, 1018.0], [78.6, 1018.0], [78.7, 1019.0], [78.8, 1019.0], [78.9, 1019.0], [79.0, 1019.0], [79.1, 1019.0], [79.2, 1019.0], [79.3, 1019.0], [79.4, 1020.0], [79.5, 1020.0], [79.6, 1020.0], [79.7, 1020.0], [79.8, 1020.0], [79.9, 1021.0], [80.0, 1021.0], [80.1, 1021.0], [80.2, 1021.0], [80.3, 1021.0], [80.4, 1022.0], [80.5, 1022.0], [80.6, 1022.0], [80.7, 1022.0], [80.8, 1022.0], [80.9, 1023.0], [81.0, 1023.0], [81.1, 1023.0], [81.2, 1024.0], [81.3, 1024.0], [81.4, 1024.0], [81.5, 1024.0], [81.6, 1025.0], [81.7, 1025.0], [81.8, 1025.0], [81.9, 1025.0], [82.0, 1025.0], [82.1, 1025.0], [82.2, 1025.0], [82.3, 1026.0], [82.4, 1026.0], [82.5, 1027.0], [82.6, 1027.0], [82.7, 1028.0], [82.8, 1028.0], [82.9, 1028.0], [83.0, 1029.0], [83.1, 1029.0], [83.2, 1030.0], [83.3, 1030.0], [83.4, 1031.0], [83.5, 1031.0], [83.6, 1032.0], [83.7, 1033.0], [83.8, 1033.0], [83.9, 1034.0], [84.0, 1034.0], [84.1, 1035.0], [84.2, 1035.0], [84.3, 1035.0], [84.4, 1035.0], [84.5, 1035.0], [84.6, 1036.0], [84.7, 1036.0], [84.8, 1036.0], [84.9, 1037.0], [85.0, 1038.0], [85.1, 1038.0], [85.2, 1039.0], [85.3, 1039.0], [85.4, 1040.0], [85.5, 1040.0], [85.6, 1041.0], [85.7, 1042.0], [85.8, 1042.0], [85.9, 1043.0], [86.0, 1043.0], [86.1, 1043.0], [86.2, 1043.0], [86.3, 1043.0], [86.4, 1043.0], [86.5, 1044.0], [86.6, 1044.0], [86.7, 1044.0], [86.8, 1044.0], [86.9, 1045.0], [87.0, 1045.0], [87.1, 1045.0], [87.2, 1045.0], [87.3, 1046.0], [87.4, 1046.0], [87.5, 1047.0], [87.6, 1047.0], [87.7, 1047.0], [87.8, 1048.0], [87.9, 1048.0], [88.0, 1048.0], [88.1, 1049.0], [88.2, 1050.0], [88.3, 1050.0], [88.4, 1051.0], [88.5, 1051.0], [88.6, 1051.0], [88.7, 1051.0], [88.8, 1052.0], [88.9, 1052.0], [89.0, 1052.0], [89.1, 1052.0], [89.2, 1052.0], [89.3, 1053.0], [89.4, 1053.0], [89.5, 1053.0], [89.6, 1053.0], [89.7, 1053.0], [89.8, 1054.0], [89.9, 1054.0], [90.0, 1054.0], [90.1, 1054.0], [90.2, 1054.0], [90.3, 1055.0], [90.4, 1055.0], [90.5, 1055.0], [90.6, 1056.0], [90.7, 1056.0], [90.8, 1056.0], [90.9, 1056.0], [91.0, 1057.0], [91.1, 1057.0], [91.2, 1058.0], [91.3, 1058.0], [91.4, 1059.0], [91.5, 1059.0], [91.6, 1059.0], [91.7, 1059.0], [91.8, 1060.0], [91.9, 1060.0], [92.0, 1060.0], [92.1, 1060.0], [92.2, 1061.0], [92.3, 1061.0], [92.4, 1061.0], [92.5, 1061.0], [92.6, 1061.0], [92.7, 1062.0], [92.8, 1062.0], [92.9, 1062.0], [93.0, 1062.0], [93.1, 1063.0], [93.2, 1063.0], [93.3, 1063.0], [93.4, 1064.0], [93.5, 1064.0], [93.6, 1064.0], [93.7, 1064.0], [93.8, 1065.0], [93.9, 1065.0], [94.0, 1065.0], [94.1, 1065.0], [94.2, 1066.0], [94.3, 1066.0], [94.4, 1066.0], [94.5, 1066.0], [94.6, 1067.0], [94.7, 1067.0], [94.8, 1067.0], [94.9, 1068.0], [95.0, 1068.0], [95.1, 1068.0], [95.2, 1068.0], [95.3, 1069.0], [95.4, 1069.0], [95.5, 1069.0], [95.6, 1070.0], [95.7, 1070.0], [95.8, 1070.0], [95.9, 1071.0], [96.0, 1071.0], [96.1, 1071.0], [96.2, 1072.0], [96.3, 1073.0], [96.4, 1073.0], [96.5, 1074.0], [96.6, 1074.0], [96.7, 1074.0], [96.8, 1075.0], [96.9, 1075.0], [97.0, 1076.0], [97.1, 1076.0], [97.2, 1077.0], [97.3, 1077.0], [97.4, 1078.0], [97.5, 1079.0], [97.6, 1080.0], [97.7, 1082.0], [97.8, 1082.0], [97.9, 1083.0], [98.0, 1083.0], [98.1, 1084.0], [98.2, 1085.0], [98.3, 1087.0], [98.4, 1088.0], [98.5, 1089.0], [98.6, 1091.0], [98.7, 1093.0], [98.8, 1102.0], [98.9, 1109.0], [99.0, 1117.0], [99.1, 1122.0], [99.2, 1143.0], [99.3, 1155.0], [99.4, 1158.0], [99.5, 1159.0], [99.6, 1165.0], [99.7, 1172.0], [99.8, 1178.0], [99.9, 1181.0], [100.0, 1246.0]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Response Time Percentiles"}},
        getOptions: function() {
            return {
                series: {
                    points: { show: false }
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentiles'
                },
                xaxis: {
                    tickDecimals: 1,
                    axisLabel: "Percentiles",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Percentile value in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : %x.2 percentile was %y ms"
                },
                selection: { mode: "xy" },
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentiles"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesPercentiles"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesPercentiles"), dataset, prepareOverviewOptions(options));
        }
};

/**
 * @param elementId Id of element where we display message
 */
function setEmptyGraph(elementId) {
    $(function() {
        $(elementId).text("No graph series with filter="+seriesFilter);
    });
}

// Response times percentiles
function refreshResponseTimePercentiles() {
    var infos = responseTimePercentilesInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimePercentiles");
        return;
    }
    if (isGraph($("#flotResponseTimesPercentiles"))){
        infos.createGraph();
    } else {
        var choiceContainer = $("#choicesResponseTimePercentiles");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesPercentiles", "#overviewResponseTimesPercentiles");
        $('#bodyResponseTimePercentiles .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimeDistributionInfos = {
        data: {"result": {"minY": 3.0, "minX": 900.0, "maxY": 3087.0, "series": [{"data": [[1100.0, 57.0], [1200.0, 3.0], [900.0, 3087.0], [1000.0, 1521.0]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 100, "maxX": 1200.0, "title": "Response Time Distribution"}},
        getOptions: function() {
            var granularity = this.data.result.granularity;
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    barWidth: this.data.result.granularity
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " responses for " + label + " were between " + xval + " and " + (xval + granularity) + " ms";
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimeDistribution"), prepareData(data.result.series, $("#choicesResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshResponseTimeDistribution() {
    var infos = responseTimeDistributionInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeDistribution");
        return;
    }
    if (isGraph($("#flotResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var syntheticResponseTimeDistributionInfos = {
        data: {"result": {"minY": 4668.0, "minX": 1.0, "ticks": [[0, "Requests having \nresponse time <= 500ms"], [1, "Requests having \nresponse time > 500ms and <= 1,500ms"], [2, "Requests having \nresponse time > 1,500ms"], [3, "Requests in error"]], "maxY": 4668.0, "series": [{"data": [], "color": "#9ACD32", "isOverall": false, "label": "Requests having \nresponse time <= 500ms", "isController": false}, {"data": [[1.0, 4668.0]], "color": "yellow", "isOverall": false, "label": "Requests having \nresponse time > 500ms and <= 1,500ms", "isController": false}, {"data": [], "color": "orange", "isOverall": false, "label": "Requests having \nresponse time > 1,500ms", "isController": false}, {"data": [], "color": "#FF6347", "isOverall": false, "label": "Requests in error", "isController": false}], "supportsControllersDiscrimination": false, "maxX": 1.0, "title": "Synthetic Response Times Distribution"}},
        getOptions: function() {
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendSyntheticResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times ranges",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                    tickLength:0,
                    min:-0.5,
                    max:3.5
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    align: "center",
                    barWidth: 0.25,
                    fill:.75
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " " + label;
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            options.xaxis.ticks = data.result.ticks;
            $.plot($("#flotSyntheticResponseTimeDistribution"), prepareData(data.result.series, $("#choicesSyntheticResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshSyntheticResponseTimeDistribution() {
    var infos = syntheticResponseTimeDistributionInfos;
    prepareSeries(infos.data, true);
    if (isGraph($("#flotSyntheticResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerSyntheticResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var activeThreadsOverTimeInfos = {
        data: {"result": {"minY": 7.60128617363344, "minX": 1.77910242E12, "maxY": 12.0, "series": [{"data": [[1.7791026E12, 12.0], [1.77910278E12, 12.0], [1.77910272E12, 12.0], [1.77910242E12, 7.60128617363344], [1.7791029E12, 12.0], [1.77910284E12, 12.0], [1.77910254E12, 12.0], [1.77910302E12, 10.270270270270272], [1.77910248E12, 12.0], [1.77910296E12, 12.0], [1.77910266E12, 12.0]], "isOverall": false, "label": "Thread Group", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910302E12, "title": "Active Threads Over Time"}},
        getOptions: function() {
            return {
                series: {
                    stack: true,
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 6,
                    show: true,
                    container: '#legendActiveThreadsOverTime'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                selection: {
                    mode: 'xy'
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : At %x there were %y active threads"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesActiveThreadsOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotActiveThreadsOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewActiveThreadsOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Active Threads Over Time
function refreshActiveThreadsOverTime(fixTimestamps) {
    var infos = activeThreadsOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotActiveThreadsOverTime"))) {
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesActiveThreadsOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotActiveThreadsOverTime", "#overviewActiveThreadsOverTime");
        $('#footerActiveThreadsOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var timeVsThreadsInfos = {
        data: {"result": {"minY": 928.6666666666667, "minX": 1.0, "maxY": 1007.4999999999999, "series": [{"data": [[1.0, 928.6666666666667], [2.0, 949.1818181818181], [4.0, 971.1818181818184], [8.0, 979.525], [9.0, 985.2727272727274], [5.0, 1007.4999999999999], [10.0, 990.5238095238095], [11.0, 992.3809523809525], [3.0, 958.6875], [6.0, 998.9032258064515], [12.0, 998.6521340064265], [7.0, 991.8666666666667]], "isOverall": false, "label": "HTTP Request", "isController": false}, {"data": [[11.693230505569845, 997.7669237360739]], "isOverall": false, "label": "HTTP Request-Aggregated", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 12.0, "title": "Time VS Threads"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: { noColumns: 2,show: true, container: '#legendTimeVsThreads' },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s: At %x.2 active threads, Average response time was %y.2 ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesTimeVsThreads"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotTimesVsThreads"), dataset, options);
            // setup overview
            $.plot($("#overviewTimesVsThreads"), dataset, prepareOverviewOptions(options));
        }
};

// Time vs threads
function refreshTimeVsThreads(){
    var infos = timeVsThreadsInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTimeVsThreads");
        return;
    }
    if(isGraph($("#flotTimesVsThreads"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTimeVsThreads");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTimesVsThreads", "#overviewTimesVsThreads");
        $('#footerTimeVsThreads .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var bytesThroughputOverTimeInfos = {
        data : {"result": {"minY": 102.36666666666666, "minX": 1.77910242E12, "maxY": 1848.0, "series": [{"data": [[1.7791026E12, 1848.0], [1.77910278E12, 1848.0], [1.77910272E12, 1848.0], [1.77910242E12, 1197.35], [1.7791029E12, 1848.0], [1.77910284E12, 1848.0], [1.77910254E12, 1848.0], [1.77910302E12, 142.45], [1.77910248E12, 1848.0], [1.77910296E12, 1848.0], [1.77910266E12, 1848.0]], "isOverall": false, "label": "Bytes received per second", "isController": false}, {"data": [[1.7791026E12, 1328.0], [1.77910278E12, 1328.0], [1.77910272E12, 1328.0], [1.77910242E12, 860.4333333333333], [1.7791029E12, 1328.0], [1.77910284E12, 1328.0], [1.77910254E12, 1328.0], [1.77910302E12, 102.36666666666666], [1.77910248E12, 1328.0], [1.77910296E12, 1328.0], [1.77910266E12, 1328.0]], "isOverall": false, "label": "Bytes sent per second", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910302E12, "title": "Bytes Throughput Over Time"}},
        getOptions : function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity) ,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Bytes / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendBytesThroughputOverTime'
                },
                selection: {
                    mode: "xy"
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y"
                }
            };
        },
        createGraph : function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesBytesThroughputOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotBytesThroughputOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewBytesThroughputOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Bytes throughput Over Time
function refreshBytesThroughputOverTime(fixTimestamps) {
    var infos = bytesThroughputOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotBytesThroughputOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesBytesThroughputOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotBytesThroughputOverTime", "#overviewBytesThroughputOverTime");
        $('#footerBytesThroughputOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimesOverTimeInfos = {
        data: {"result": {"minY": 984.9356913183278, "minX": 1.77910242E12, "maxY": 1006.6770833333333, "series": [{"data": [[1.7791026E12, 1001.7270833333328], [1.77910278E12, 1001.48125], [1.77910272E12, 1006.6770833333333], [1.77910242E12, 984.9356913183278], [1.7791029E12, 1000.3875000000003], [1.77910284E12, 999.2604166666669], [1.77910254E12, 991.6354166666675], [1.77910302E12, 992.8108108108108], [1.77910248E12, 987.7458333333337], [1.77910296E12, 997.5791666666669], [1.77910266E12, 1002.1041666666665]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910302E12, "title": "Response Time Over Time"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average response time was %y ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Times Over Time
function refreshResponseTimeOverTime(fixTimestamps) {
    var infos = responseTimesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotResponseTimesOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesOverTime", "#overviewResponseTimesOverTime");
        $('#footerResponseTimesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var latenciesOverTimeInfos = {
        data: {"result": {"minY": 984.8327974276526, "minX": 1.77910242E12, "maxY": 1006.6562500000008, "series": [{"data": [[1.7791026E12, 1001.704166666667], [1.77910278E12, 1001.4562500000006], [1.77910272E12, 1006.6562500000008], [1.77910242E12, 984.8327974276526], [1.7791029E12, 1000.3708333333334], [1.77910284E12, 999.25], [1.77910254E12, 991.5875000000001], [1.77910302E12, 992.756756756757], [1.77910248E12, 987.7104166666667], [1.77910296E12, 997.5750000000004], [1.77910266E12, 1002.0812499999995]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910302E12, "title": "Latencies Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response latencies in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendLatenciesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average latency was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesLatenciesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotLatenciesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewLatenciesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Latencies Over Time
function refreshLatenciesOverTime(fixTimestamps) {
    var infos = latenciesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyLatenciesOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotLatenciesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesLatenciesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotLatenciesOverTime", "#overviewLatenciesOverTime");
        $('#footerLatenciesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var connectTimeOverTimeInfos = {
        data: {"result": {"minY": 0.08958333333333335, "minX": 1.77910242E12, "maxY": 0.3922829581993568, "series": [{"data": [[1.7791026E12, 0.17708333333333345], [1.77910278E12, 0.1479166666666668], [1.77910272E12, 0.1458333333333332], [1.77910242E12, 0.3922829581993568], [1.7791029E12, 0.13958333333333356], [1.77910284E12, 0.19583333333333322], [1.77910254E12, 0.13125000000000017], [1.77910302E12, 0.18918918918918926], [1.77910248E12, 0.16041666666666685], [1.77910296E12, 0.08958333333333335], [1.77910266E12, 0.12499999999999975]], "isOverall": false, "label": "HTTP Request", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910302E12, "title": "Connect Time Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getConnectTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average Connect Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendConnectTimeOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average connect time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesConnectTimeOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotConnectTimeOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewConnectTimeOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Connect Time Over Time
function refreshConnectTimeOverTime(fixTimestamps) {
    var infos = connectTimeOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyConnectTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotConnectTimeOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesConnectTimeOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotConnectTimeOverTime", "#overviewConnectTimeOverTime");
        $('#footerConnectTimeOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var responseTimePercentilesOverTimeInfos = {
        data: {"result": {"minY": 907.0, "minX": 1.77910242E12, "maxY": 1246.0, "series": [{"data": [[1.7791026E12, 1180.0], [1.77910278E12, 1143.0], [1.77910272E12, 1244.0], [1.77910242E12, 1059.0], [1.7791029E12, 1246.0], [1.77910284E12, 1165.0], [1.77910254E12, 1186.0], [1.77910302E12, 1073.0], [1.77910248E12, 1181.0], [1.77910296E12, 1157.0], [1.77910266E12, 1181.0]], "isOverall": false, "label": "Max", "isController": false}, {"data": [[1.7791026E12, 970.0], [1.77910278E12, 969.0], [1.77910272E12, 970.0], [1.77910242E12, 907.0], [1.7791029E12, 970.0], [1.77910284E12, 969.0], [1.77910254E12, 970.0], [1.77910302E12, 971.0], [1.77910248E12, 970.0], [1.77910296E12, 969.0], [1.77910266E12, 969.0]], "isOverall": false, "label": "Min", "isController": false}, {"data": [[1.7791026E12, 1055.0], [1.77910278E12, 1057.9], [1.77910272E12, 1068.0], [1.77910242E12, 1022.0], [1.7791029E12, 1060.0], [1.77910284E12, 1058.8000000000002], [1.77910254E12, 1033.8000000000002], [1.77910302E12, 1039.0], [1.77910248E12, 1019.0], [1.77910296E12, 1054.9], [1.77910266E12, 1063.0]], "isOverall": false, "label": "90th percentile", "isController": false}, {"data": [[1.7791026E12, 1142.1699999999998], [1.77910278E12, 1109.71], [1.77910272E12, 1109.0], [1.77910242E12, 1038.88], [1.7791029E12, 1159.0], [1.77910284E12, 1155.0], [1.77910254E12, 1111.2099999999998], [1.77910302E12, 1073.0], [1.77910248E12, 1079.9], [1.77910296E12, 1077.0], [1.77910266E12, 1172.0]], "isOverall": false, "label": "99th percentile", "isController": false}, {"data": [[1.7791026E12, 983.0], [1.77910278E12, 983.0], [1.77910272E12, 992.0], [1.77910242E12, 981.0], [1.7791029E12, 982.0], [1.77910284E12, 981.0], [1.77910254E12, 979.0], [1.77910302E12, 980.0], [1.77910248E12, 979.0], [1.77910296E12, 981.0], [1.77910266E12, 984.5]], "isOverall": false, "label": "Median", "isController": false}, {"data": [[1.7791026E12, 1072.9], [1.77910278E12, 1069.95], [1.77910272E12, 1082.0], [1.77910242E12, 1024.0], [1.7791029E12, 1068.9], [1.77910284E12, 1072.0], [1.77910254E12, 1058.95], [1.77910302E12, 1073.0], [1.77910248E12, 1046.0], [1.77910296E12, 1064.0], [1.77910266E12, 1070.95]], "isOverall": false, "label": "95th percentile", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910302E12, "title": "Response Time Percentiles Over Time (successful requests only)"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Response Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentilesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Response time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentilesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimePercentilesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimePercentilesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Time Percentiles Over Time
function refreshResponseTimePercentilesOverTime(fixTimestamps) {
    var infos = responseTimePercentilesOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotResponseTimePercentilesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimePercentilesOverTime", "#overviewResponseTimePercentilesOverTime");
        $('#footerResponseTimePercentilesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var responseTimeVsRequestInfos = {
    data: {"result": {"minY": 909.0, "minX": 1.0, "maxY": 1023.0, "series": [{"data": [[1.0, 909.0], [2.0, 982.0], [4.0, 949.0], [8.0, 972.0], [9.0, 978.0], [5.0, 1023.0], [10.0, 981.5], [11.0, 994.0], [3.0, 998.0], [6.0, 998.0], [12.0, 981.0], [7.0, 1011.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 12.0, "title": "Response Time Vs Request"}},
    getOptions: function() {
        return {
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Response Time in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: {
                noColumns: 2,
                show: true,
                container: '#legendResponseTimeVsRequest'
            },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median response time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesResponseTimeVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotResponseTimeVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewResponseTimeVsRequest"), dataset, prepareOverviewOptions(options));

    }
};

// Response Time vs Request
function refreshResponseTimeVsRequest() {
    var infos = responseTimeVsRequestInfos;
    prepareSeries(infos.data);
    if (isGraph($("#flotResponseTimeVsRequest"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeVsRequest");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimeVsRequest", "#overviewResponseTimeVsRequest");
        $('#footerResponseRimeVsRequest .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var latenciesVsRequestInfos = {
    data: {"result": {"minY": 909.0, "minX": 1.0, "maxY": 1023.0, "series": [{"data": [[1.0, 909.0], [2.0, 982.0], [4.0, 949.0], [8.0, 972.0], [9.0, 978.0], [5.0, 1023.0], [10.0, 981.5], [11.0, 993.5], [3.0, 998.0], [6.0, 998.0], [12.0, 981.0], [7.0, 1011.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 12.0, "title": "Latencies Vs Request"}},
    getOptions: function() {
        return{
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Latency in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: { noColumns: 2,show: true, container: '#legendLatencyVsRequest' },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median Latency time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesLatencyVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotLatenciesVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewLatenciesVsRequest"), dataset, prepareOverviewOptions(options));
    }
};

// Latencies vs Request
function refreshLatenciesVsRequest() {
        var infos = latenciesVsRequestInfos;
        prepareSeries(infos.data);
        if(isGraph($("#flotLatenciesVsRequest"))){
            infos.createGraph();
        }else{
            var choiceContainer = $("#choicesLatencyVsRequest");
            createLegend(choiceContainer, infos);
            infos.createGraph();
            setGraphZoomable("#flotLatenciesVsRequest", "#overviewLatenciesVsRequest");
            $('#footerLatenciesVsRequest .legendColorBox > div').each(function(i){
                $(this).clone().prependTo(choiceContainer.find("li").eq(i));
            });
        }
};

var hitsPerSecondInfos = {
        data: {"result": {"minY": 0.5666666666666667, "minX": 1.77910242E12, "maxY": 8.0, "series": [{"data": [[1.7791026E12, 8.0], [1.77910278E12, 8.0], [1.77910272E12, 8.0], [1.77910242E12, 5.233333333333333], [1.7791029E12, 8.0], [1.77910284E12, 8.0], [1.77910254E12, 8.0], [1.77910302E12, 0.5666666666666667], [1.77910248E12, 8.0], [1.77910296E12, 8.0], [1.77910266E12, 8.0]], "isOverall": false, "label": "hitsPerSecond", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910302E12, "title": "Hits Per Second"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of hits / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendHitsPerSecond"
                },
                selection: {
                    mode : 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y.2 hits/sec"
                }
            };
        },
        createGraph: function createGraph() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesHitsPerSecond"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotHitsPerSecond"), dataset, options);
            // setup overview
            $.plot($("#overviewHitsPerSecond"), dataset, prepareOverviewOptions(options));
        }
};

// Hits per second
function refreshHitsPerSecond(fixTimestamps) {
    var infos = hitsPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if (isGraph($("#flotHitsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesHitsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotHitsPerSecond", "#overviewHitsPerSecond");
        $('#footerHitsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var codesPerSecondInfos = {
        data: {"result": {"minY": 0.6166666666666667, "minX": 1.77910242E12, "maxY": 8.0, "series": [{"data": [[1.7791026E12, 8.0], [1.77910278E12, 8.0], [1.77910272E12, 8.0], [1.77910242E12, 5.183333333333334], [1.7791029E12, 8.0], [1.77910284E12, 8.0], [1.77910254E12, 8.0], [1.77910302E12, 0.6166666666666667], [1.77910248E12, 8.0], [1.77910296E12, 8.0], [1.77910266E12, 8.0]], "isOverall": false, "label": "200", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77910302E12, "title": "Codes Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendCodesPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "Number of Response Codes %s at %x was %y.2 responses / sec"
                }
            };
        },
    createGraph: function() {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesCodesPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotCodesPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewCodesPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Codes per second
function refreshCodesPerSecond(fixTimestamps) {
    var infos = codesPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotCodesPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesCodesPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotCodesPerSecond", "#overviewCodesPerSecond");
        $('#footerCodesPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var transactionsPerSecondInfos = {
        data: {"result": {"minY": 0.6166666666666667, "minX": 1.77910242E12, "maxY": 8.0, "series": [{"data": [[1.7791026E12, 8.0], [1.77910278E12, 8.0], [1.77910272E12, 8.0], [1.77910242E12, 5.183333333333334], [1.7791029E12, 8.0], [1.77910284E12, 8.0], [1.77910254E12, 8.0], [1.77910302E12, 0.6166666666666667], [1.77910248E12, 8.0], [1.77910296E12, 8.0], [1.77910266E12, 8.0]], "isOverall": false, "label": "HTTP Request-success", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910302E12, "title": "Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTransactionsPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                }
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTransactionsPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTransactionsPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewTransactionsPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Transactions per second
function refreshTransactionsPerSecond(fixTimestamps) {
    var infos = transactionsPerSecondInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTransactionsPerSecond");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotTransactionsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTransactionsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTransactionsPerSecond", "#overviewTransactionsPerSecond");
        $('#footerTransactionsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var totalTPSInfos = {
        data: {"result": {"minY": 0.6166666666666667, "minX": 1.77910242E12, "maxY": 8.0, "series": [{"data": [[1.7791026E12, 8.0], [1.77910278E12, 8.0], [1.77910272E12, 8.0], [1.77910242E12, 5.183333333333334], [1.7791029E12, 8.0], [1.77910284E12, 8.0], [1.77910254E12, 8.0], [1.77910302E12, 0.6166666666666667], [1.77910248E12, 8.0], [1.77910296E12, 8.0], [1.77910266E12, 8.0]], "isOverall": false, "label": "Transaction-success", "isController": false}, {"data": [], "isOverall": false, "label": "Transaction-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77910302E12, "title": "Total Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTotalTPS"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                },
                colors: ["#9ACD32", "#FF6347"]
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTotalTPS"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTotalTPS"), dataset, options);
        // setup overview
        $.plot($("#overviewTotalTPS"), dataset, prepareOverviewOptions(options));
    }
};

// Total Transactions per second
function refreshTotalTPS(fixTimestamps) {
    var infos = totalTPSInfos;
    // We want to ignore seriesFilter
    prepareSeries(infos.data, false, true);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotTotalTPS"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTotalTPS");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTotalTPS", "#overviewTotalTPS");
        $('#footerTotalTPS .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

// Collapse the graph matching the specified DOM element depending the collapsed
// status
function collapse(elem, collapsed){
    if(collapsed){
        $(elem).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    } else {
        $(elem).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
        if (elem.id == "bodyBytesThroughputOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshBytesThroughputOverTime(true);
            }
            document.location.href="#bytesThroughputOverTime";
        } else if (elem.id == "bodyLatenciesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesOverTime(true);
            }
            document.location.href="#latenciesOverTime";
        } else if (elem.id == "bodyCustomGraph") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCustomGraph(true);
            }
            document.location.href="#responseCustomGraph";
        } else if (elem.id == "bodyConnectTimeOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshConnectTimeOverTime(true);
            }
            document.location.href="#connectTimeOverTime";
        } else if (elem.id == "bodyResponseTimePercentilesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimePercentilesOverTime(true);
            }
            document.location.href="#responseTimePercentilesOverTime";
        } else if (elem.id == "bodyResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeDistribution();
            }
            document.location.href="#responseTimeDistribution" ;
        } else if (elem.id == "bodySyntheticResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshSyntheticResponseTimeDistribution();
            }
            document.location.href="#syntheticResponseTimeDistribution" ;
        } else if (elem.id == "bodyActiveThreadsOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshActiveThreadsOverTime(true);
            }
            document.location.href="#activeThreadsOverTime";
        } else if (elem.id == "bodyTimeVsThreads") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTimeVsThreads();
            }
            document.location.href="#timeVsThreads" ;
        } else if (elem.id == "bodyCodesPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCodesPerSecond(true);
            }
            document.location.href="#codesPerSecond";
        } else if (elem.id == "bodyTransactionsPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTransactionsPerSecond(true);
            }
            document.location.href="#transactionsPerSecond";
        } else if (elem.id == "bodyTotalTPS") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTotalTPS(true);
            }
            document.location.href="#totalTPS";
        } else if (elem.id == "bodyResponseTimeVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeVsRequest();
            }
            document.location.href="#responseTimeVsRequest";
        } else if (elem.id == "bodyLatenciesVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesVsRequest();
            }
            document.location.href="#latencyVsRequest";
        }
    }
}

/*
 * Activates or deactivates all series of the specified graph (represented by id parameter)
 * depending on checked argument.
 */
function toggleAll(id, checked){
    var placeholder = document.getElementById(id);

    var cases = $(placeholder).find(':checkbox');
    cases.prop('checked', checked);
    $(cases).parent().children().children().toggleClass("legend-disabled", !checked);

    var choiceContainer;
    if ( id == "choicesBytesThroughputOverTime"){
        choiceContainer = $("#choicesBytesThroughputOverTime");
        refreshBytesThroughputOverTime(false);
    } else if(id == "choicesResponseTimesOverTime"){
        choiceContainer = $("#choicesResponseTimesOverTime");
        refreshResponseTimeOverTime(false);
    }else if(id == "choicesResponseCustomGraph"){
        choiceContainer = $("#choicesResponseCustomGraph");
        refreshCustomGraph(false);
    } else if ( id == "choicesLatenciesOverTime"){
        choiceContainer = $("#choicesLatenciesOverTime");
        refreshLatenciesOverTime(false);
    } else if ( id == "choicesConnectTimeOverTime"){
        choiceContainer = $("#choicesConnectTimeOverTime");
        refreshConnectTimeOverTime(false);
    } else if ( id == "choicesResponseTimePercentilesOverTime"){
        choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        refreshResponseTimePercentilesOverTime(false);
    } else if ( id == "choicesResponseTimePercentiles"){
        choiceContainer = $("#choicesResponseTimePercentiles");
        refreshResponseTimePercentiles();
    } else if(id == "choicesActiveThreadsOverTime"){
        choiceContainer = $("#choicesActiveThreadsOverTime");
        refreshActiveThreadsOverTime(false);
    } else if ( id == "choicesTimeVsThreads"){
        choiceContainer = $("#choicesTimeVsThreads");
        refreshTimeVsThreads();
    } else if ( id == "choicesSyntheticResponseTimeDistribution"){
        choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        refreshSyntheticResponseTimeDistribution();
    } else if ( id == "choicesResponseTimeDistribution"){
        choiceContainer = $("#choicesResponseTimeDistribution");
        refreshResponseTimeDistribution();
    } else if ( id == "choicesHitsPerSecond"){
        choiceContainer = $("#choicesHitsPerSecond");
        refreshHitsPerSecond(false);
    } else if(id == "choicesCodesPerSecond"){
        choiceContainer = $("#choicesCodesPerSecond");
        refreshCodesPerSecond(false);
    } else if ( id == "choicesTransactionsPerSecond"){
        choiceContainer = $("#choicesTransactionsPerSecond");
        refreshTransactionsPerSecond(false);
    } else if ( id == "choicesTotalTPS"){
        choiceContainer = $("#choicesTotalTPS");
        refreshTotalTPS(false);
    } else if ( id == "choicesResponseTimeVsRequest"){
        choiceContainer = $("#choicesResponseTimeVsRequest");
        refreshResponseTimeVsRequest();
    } else if ( id == "choicesLatencyVsRequest"){
        choiceContainer = $("#choicesLatencyVsRequest");
        refreshLatenciesVsRequest();
    }
    var color = checked ? "black" : "#818181";
    if(choiceContainer != null) {
        choiceContainer.find("label").each(function(){
            this.style.color = color;
        });
    }
}

