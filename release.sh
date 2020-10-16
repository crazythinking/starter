# 添加所有修改的内容
git add .
# 批量修改pom文件版本为相应RELEASE
sed -i "s/1.0.0-SNAPSHOT/1.0.0.RELEASE/g" pom.xml */pom.xml */*/pom.xml
# 编译打包RELEASE
mvn clean deploy
# 添加所有修改的内容
git add .
# 提交本地分支
git commit -m '切换到1.0.0.RELEASE'
# 提交到远程分支
git push origin 1.0.0
# 创建相应版本的RELEASE tag
git tag -a v1.0.0.RELEASE -m "1.0.0.RELEASE版本"
# 提交tag
git push --tags
#切换到master分支
git checkout master
# 合并目标版本到master分支
git merge 1.0.0
# 添加所有修改的内容
git add .
# 提交本地分支
git commit -m '合并分支1.0.0'
# 提交合并
git push origin master
# 创建切换到新创建的分支
git checkout -b 1.1.0
# 将master提升版本号
sed -i "s/1.0.0.RELEASE/1.1.0-SNAPSHOT/g" pom.xml */pom.xml */*/pom.xml
# 添加所有修改的内容
git add .
# 提交本地master分支
git commit -m '切换到1.1.0'
# 提交到远程master分支
git push origin 1.1.0

