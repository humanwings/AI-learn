import re

def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(0, n-i-1):
            if arr[j] > arr[j+1]:
                arr[j], arr[j+1] = arr[j+1], arr[j]
    return arr

# 示例使用
if __name__ == "__main__":
    arr = [64, 34, 25, 12, 22, 11, 90]
    sorted_arr = bubble_sort(arr)
    print("排序后的数组:", sorted_arr)


class InputChecker:
    def __init__(self, input_str):
        self.input_str = input_str

    def contains_cjk(self):
        return bool(re.search(r'[\u4e00-\u9fff\u3040-\u30ff]', self.input_str))

# 示例使用
checker = InputChecker("こんにちは")
print("包含汉字、日文汉字或假名:", checker.contains_cjk())