/* eslint-disable @typescript-eslint/no-var-requires */
const {merge} = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
	mode: 'development',
	devtool: 'source-map',
	devServer: {
		port: 7584,
		open: {
			target: ['index.html'],
			app: {
				name: 'chrome',	// The browser application name is platform-dependent. Don't hard code it in reusable modules. For example, 'Chrome' is 'Google Chrome' on macOS, 'google-chrome' on Linux, and 'chrome' on Windows.
				//arguments: ['--incognito', '--new-window'],
			},
		},
	},
});
