#ifndef __CNativeVideoImpl
#define __CNativeVideoImpl

#include <string>
using std::string;

class NativeVideoImpl {

	string videoSource;

	public:
		NativeVideoImpl (string src);
		int** fetchNextFrame();
		string getSource(){ return videoSource; }

};
#endif