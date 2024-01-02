const path = require('path');
const merge = require('webpack-merge');
const webpack = require('webpack');
//const NpmInstallPlugin = require('npm-install-webpack-plugin');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
// Load *package.json* so we can use `dependencies` from there
const pkg = require('./package.json');

const TARGET = process.env.npm_lifecycle_event;
const PATHS = {
  app: path.join(__dirname, 'app'),
  solution: path.join(__dirname, 'solution'),
  build: path.join(__dirname, 'build')
};

process.env.BABEL_ENV = TARGET;

const common = {
    entry: {
       // 'legacy': './app/legacy.js',
        'vendor': './app/vendor.js',
        'app': './app/index.jsx' // our app
       // 'solution': './app/solution.js'
    },
    resolve: {
        extensions: ['.js', '.jsx']
    },
    output: {
      path: PATHS.build,
        filename: /*isProd ? 'js/[name].[hash].js' :*/ 'js/[name].js',
        chunkFilename: /*isProd ? '[id].[hash].chunk.js' :*/ '[id].chunk.js',
      // filename: 'bundle.js'
      // publicPath: "/assets/"
        // Output using entry name
        // filename: '[name].js'
    },
  /*resolve: {
    alias: {
        jquery: "jquery/src/jquery",
        'jquery-ui': "jquery-ui/jquery-ui.js",  
    }
  },*/
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        use: { loader: "babel-loader", options: 'cacheDirectory' },
        include: [ PATHS.app, PATHS.solution ]
      },
      {
        test: /\.css$/,
        loaders: ['style-loader', 'css-loader']
      },
      {
        test: /\.scss$/,
        use: [ "style-loader", "css-loader", "sass-loader"],
        include: PATHS.app
      },
      {
        test: /\.(jpe|jpg|woff|woff2|eot|ttf|svg)(\?.*$|$)/, //font-awesome
        use: ['file-loader']
      },
      {
        test: /\.(png|gif)$/, //jquery ui, jstree
        use: ['file-loader']
      },
      {
        test: require.resolve('jquery'),
        use: [{loader: 'expose-loader', query: 'jQuery'},{loader: 'expose-loader', options: '$'}]
      },
      {
        test: require.resolve('jquery-ui'),
        use: [{loader: 'expose-loader', query: '$.datepicker'}]},
      { 
        test: '/jstree/dist/jstree.js',
        use: [{loader: 'imports-loader', query: 'jQuery=jquery,this=>window'}]
      }
    ]
  },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
          name: [/*'legacy',*/ 'vendor'/*, 'solution'*/]
        })
    ]
    /*,
  plugins: [
    new webpack.ProvidePlugin({
      "$":"jquery",
      "jQuery":"jquery",
      "window.jQuery":"jquery"
    })
  ],*/
};

if(TARGET === 'start' || !TARGET) {
  module.exports = merge(common, {
    //devtool: 'eval-source-map',
    devtool: 'eval',
    //devtool: 'inline-]source-map',
    devServer: {
        contentBase: PATHS.build,
        historyApiFallback: true,
        quiet: true,
        stats: 'errors-only'
    },
    plugins: [
      new webpack.optimize.CommonsChunkPlugin({
        name: [/*'legacy',*/ 'vendor'/*, 'solution'*/]
      }),
        new HtmlWebpackPlugin({
            template: './www/index.html',
            chunksSortMode: 'dependency'
        }),
      new webpack.HotModuleReplacementPlugin(),
        // new BundleAnalyzerPlugin(
        //     {
        //         analyzerMode: 'static'
        //     }
        // )
      /*new NpmInstallPlugin({
        save: true // --save
      })*/
      //Moves files
      /*new TransferWebpackPlugin([
        {from: 'www'}
      ], path.resolve(__dirname, "src"))*/
      ]
  });
}

if(TARGET === 'build' || TARGET === 'stats') {
  module.exports = merge(common, {
    // Define vendor entry point needed for splitting
    /*entry: {
      vendor: Object.keys(pkg.dependencies).filter(function(v) {
        // Exclude alt-utils as it won't work with this setup
        // due to the way the package has been designed
        // (no package.json main).
        return v !== 'alt-utils';
      })
    },*/
    plugins: [
      new webpack.DefinePlugin({
        'process.env.NODE_ENV': '"production"'
      }),
      new webpack.optimize.UglifyJsPlugin({
        compress: {
          warnings: false
        }
      })
    ]
  });
}
