cmake_minimum_required(VERSION 3.29)
project(launcher)

set_property(GLOBAL PROPERTY USE_FOLDERS ON)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED YES)

find_package(JNI REQUIRED)
include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})

add_subdirectory(RED4ext)
set_target_properties(RED4ext.SDK PROPERTIES FOLDER "Dependencies")
mark_as_advanced(
        RED4EXT_BUILD_EXAMPLES
        RED4EXT_HEADER_ONLY
)


file(GLOB_RECURSE SRC src/*.cpp)

add_library(launcher SHARED ${SRC})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /MT")
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(${CMAKE_PROJECT_NAME} RED4ext::SDK ${JAVA_JVM_LIBRARY} ShLwApi)
