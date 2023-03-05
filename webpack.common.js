/* eslint-disable @typescript-eslint/no-var-requires */
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CopyPlugin = require('copy-webpack-plugin');
const ESLintPlugin = require('eslint-webpack-plugin');

module.exports = {
  entry: './src/index.ts',
  mode: 'development', //none/development/production
  devtool: 'source-map',
  optimization: {
    usedExports: true
  },
  output: {
	filename: "bundle.js",
    //filename: '[name].[contenthash].js',
    path: path.resolve(__dirname, 'dist')
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: {
          loader: 'ts-loader',
          options: {
            // disable type checker - we will use it in fork plugin
            transpileOnly: true
          }
        }
      },
      {
        test: /\.(scss|sass|css)$/,
        use: [
          process.env.NODE_ENV !== 'production'
            ? 'style-loader'
            : MiniCssExtractPlugin.loader,
          'css-loader',
          {
            loader: 'sass-loader',
            options: {
              sourceMap: true
            }
          }
        ]
      }
    ]
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js']
  },
  plugins: [
    new MiniCssExtractPlugin({
      // Options similar to the same options in webpackOptions.output
      // both options are optional
      //filename: '[name].[contenthash].css',
		filename: 'bundle.css',
      chunkFilename: '[id].css'
    }),
    new CleanWebpackPlugin(),// to clean dist folder each time when webpack does a new build.
    new HtmlWebpackPlugin({ // to generate index.html file from a template with scripts and styles tags.
      template: './src/index.html',
      filename: 'index.html'
    }),
    new ForkTsCheckerWebpackPlugin(), // this plugin allows us to check typescript typings as a separate process. It will improve build performance
    new CopyPlugin({ // This plugin gives us the ability to copy the assets folder with its content to the dist folder
      patterns: [{ from: 'src/assets', to: 'assets' }]
    }),
    new ESLintPlugin({
      extensions: ['.tsx', '.ts', '.js'],
      exclude: 'node_modules',
      context: 'src'
    })
  ]
};
